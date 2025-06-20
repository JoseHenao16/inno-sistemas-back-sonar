package com.udea.fe.service;

import com.udea.fe.DTO.ProjectDTO;
import com.udea.fe.entity.Project;
import com.udea.fe.entity.ProjectStatus;
import com.udea.fe.entity.User;
import com.udea.fe.entity.UserTeam;
import com.udea.fe.exception.InvalidProjectDataException;
import com.udea.fe.exception.ProjectNotFoundException;
import com.udea.fe.exception.UserNotFoundException;
import com.udea.fe.mapper.ProjectMapper;
import com.udea.fe.repository.ProjectRepository;
import com.udea.fe.repository.UserRepository;
import com.udea.fe.repository.UserTeamRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final UserTeamRepository userTeamRepository;
    private final ModelMapper modelMapper;
    private final ProjectMapper projectMapper;

    public ProjectDTO createProject(ProjectDTO dto) {
        validarDatosProyecto(dto);
        User creador = buscarCreador(dto.getCreatedById());
        Project proyecto = modelMapper.map(dto, Project.class);
        proyecto.setCreatedBy(creador);
        proyecto.setStatus(ProjectStatus.IN_PROGRESS);
        return modelMapper.map(projectRepository.save(proyecto), ProjectDTO.class);
    }

    private void validarDatosProyecto(ProjectDTO dto) {
        if (isNullOrBlank(dto.getName())) {
            throw new InvalidProjectDataException("El nombre del proyecto es obligatorio");
        }
        if (dto.getStartDate() == null) {
            throw new InvalidProjectDataException("La fecha de inicio es obligatoria");
        }
        if (dto.getEndDate() == null) {
            throw new InvalidProjectDataException("La fecha de fin es obligatoria");
        }
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new InvalidProjectDataException("La fecha de fin no puede ser anterior a la de inicio");
        }
        if (dto.getCreatedById() == null) {
            throw new InvalidProjectDataException("Debe especificarse el ID del creador del proyecto");
        }
    }

    private boolean isNullOrBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    private User buscarCreador(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario creador no encontrado"));
    }

    public ProjectDTO getProjectById(Long id) {
        return projectRepository.findById(id)
                .map(project -> modelMapper.map(project, ProjectDTO.class))
                .orElseThrow(() -> new ProjectNotFoundException("Proyecto no encontrado con id: " + id));
    }

    public List<ProjectDTO> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(project -> modelMapper.map(project, ProjectDTO.class))
                .toList();
    }

    public ProjectDTO updateProject(Long id, ProjectDTO dto) {
        return projectRepository.findById(id)
                .map(existing -> {
                    ProjectStatus statusOriginal = existing.getStatus();
                    modelMapper.map(dto, existing);
                    existing.setStatus(statusOriginal);
                    return modelMapper.map(projectRepository.save(existing), ProjectDTO.class);
                })
                .orElseThrow(() -> new ProjectNotFoundException("Proyecto no encontrado"));
    }

    public ProjectDTO changeProjectStatus(Long id, ProjectStatus nuevoEstado) {
        Project proyecto = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Proyecto no encontrado con id: " + id));
        validarTransicionEstado(proyecto.getStatus(), nuevoEstado);

        proyecto.setStatus(nuevoEstado);
        if (requiereFechaFin(nuevoEstado, proyecto)) {
            proyecto.setEndDate(LocalDate.now());
        }

        return modelMapper.map(projectRepository.save(proyecto), ProjectDTO.class);
    }

    private void validarTransicionEstado(ProjectStatus actual, ProjectStatus nuevo) {
        if (actual == nuevo) {
            throw new IllegalStateException("Ya está en estado " + nuevo);
        }
        if (actual == ProjectStatus.CANCELED && nuevo == ProjectStatus.COMPLETED) {
            throw new IllegalStateException("No se puede finalizar un proyecto cancelado");
        }
        if ((actual == ProjectStatus.CANCELED || actual == ProjectStatus.COMPLETED) && nuevo == ProjectStatus.IN_PROGRESS) {
            throw new IllegalStateException("No se puede reactivar un proyecto " + actual.name().toLowerCase());
        }
    }

    private boolean requiereFechaFin(ProjectStatus nuevoEstado, Project proyecto) {
        return (nuevoEstado == ProjectStatus.COMPLETED && proyecto.getEndDate() == null)
                || nuevoEstado == ProjectStatus.CANCELED;
    }

    public List<ProjectDTO> getProjectsByUserId(Long userId) {
        List<UserTeam> equipos = userTeamRepository.findByIdUserId(userId);
        Set<Project> proyectosEquipo = equipos.stream()
                .map(ut -> ut.getTeam().getProject())
                .collect(Collectors.toSet());
        List<Project> creados = projectRepository.findByCreatedByUserId(userId);
        proyectosEquipo.addAll(creados);
        return proyectosEquipo.stream().map(projectMapper::toDTO).toList();
    }
}