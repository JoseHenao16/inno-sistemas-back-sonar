package com.udea.fe.service;

import com.udea.fe.DTO.ProjectDTO;
import com.udea.fe.entity.*;
import com.udea.fe.exception.*;
import com.udea.fe.mapper.ProjectMapper;
import com.udea.fe.repository.*;
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
        validarDatos(dto);
        User createdBy = userRepository.findById(dto.getCreatedById())
                .orElseThrow(() -> new UserNotFoundException("Usuario creador no encontrado"));

        Project project = modelMapper.map(dto, Project.class);
        project.setCreatedBy(createdBy);
        project.setStatus(ProjectStatus.IN_PROGRESS);

        return modelMapper.map(projectRepository.save(project), ProjectDTO.class);
    }

    private void validarDatos(ProjectDTO dto) {
        if (isNuloOVacio(dto.getName())) {
            throw new InvalidProjectDataException("El nombre del proyecto es obligatorio");
        }
        if (dto.getStartDate() == null || dto.getEndDate() == null) {
            throw new InvalidProjectDataException("Las fechas de inicio y fin son obligatorias");
        }
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new InvalidProjectDataException("La fecha de finalización no puede ser anterior a la de inicio");
        }
        if (dto.getCreatedById() == null) {
            throw new InvalidProjectDataException("Debe especificarse el ID del creador del proyecto");
        }
    }

    private boolean isNuloOVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    public ProjectDTO getProjectById(Long id) {
        return projectRepository.findById(id)
                .map(p -> modelMapper.map(p, ProjectDTO.class))
                .orElseThrow(() -> new ProjectNotFoundException("Proyecto no encontrado con id: " + id));
    }

    public List<ProjectDTO> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(p -> modelMapper.map(p, ProjectDTO.class))
                .toList();
    }

    public ProjectDTO updateProject(Long id, ProjectDTO dto) {
        return projectRepository.findById(id).map(existing -> {
            ProjectStatus original = existing.getStatus();
            modelMapper.map(dto, existing);
            existing.setStatus(original);
            return modelMapper.map(projectRepository.save(existing), ProjectDTO.class);
        }).orElseThrow(() -> new ProjectNotFoundException("Proyecto no encontrado"));
    }

    public ProjectDTO changeProjectStatus(Long id, ProjectStatus nuevoEstado) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Proyecto no encontrado con id: " + id));

        validarTransicionEstado(project.getStatus(), nuevoEstado);
        aplicarCambioDeEstado(project, nuevoEstado);

        return modelMapper.map(projectRepository.save(project), ProjectDTO.class);
    }

    private void aplicarCambioDeEstado(Project project, ProjectStatus nuevoEstado) {
        project.setStatus(nuevoEstado);
        if (nuevoEstado == ProjectStatus.COMPLETED || nuevoEstado == ProjectStatus.CANCELED) {
            if (project.getEndDate() == null) {
                project.setEndDate(LocalDate.now());
            }
        }
    }

    private void validarTransicionEstado(ProjectStatus actual, ProjectStatus nuevo) {
        if (actual == nuevo) {
            throw new IllegalStateException("El proyecto ya está en estado " + nuevo);
        }
        if ((actual == ProjectStatus.COMPLETED || actual == ProjectStatus.CANCELED)
                && nuevo == ProjectStatus.IN_PROGRESS) {
            throw new IllegalStateException("No se puede reactivar un proyecto " + actual.name().toLowerCase());
        }
        if (actual == ProjectStatus.CANCELED && nuevo == ProjectStatus.COMPLETED) {
            throw new IllegalStateException("No se puede finalizar un proyecto abandonado");
        }
    }

    public List<ProjectDTO> getProjectsByUserId(Long userId) {
        Set<Project> teamProjects = userTeamRepository.findByIdUserId(userId).stream()
                .map(ut -> ut.getTeam().getProject())
                .collect(Collectors.toSet());

        teamProjects.addAll(projectRepository.findByCreatedByUserId(userId));

        return teamProjects.stream()
                .map(projectMapper::toDTO)
                .toList();
    }
}

