package com.udea.fe.service;

import com.udea.fe.DTO.ProjectDTO;
import com.udea.fe.entity.Project;
import com.udea.fe.entity.ProjectStatus;
import com.udea.fe.entity.User;
import com.udea.fe.repository.ProjectRepository;
import com.udea.fe.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public ProjectDTO createProject(ProjectDTO projectDTO) {

        if (projectDTO.getName() == null || projectDTO.getName().trim().isEmpty()) {
            throw new RuntimeException("El nombre del proyecto es obligatorio");
        }

        if (projectDTO.getStartDate() == null || projectDTO.getEndDate() == null) {
            throw new RuntimeException("Las fechas de inicio y fin son obligatorias");
        }

        if (projectDTO.getEndDate().isBefore(projectDTO.getStartDate())) {
            throw new RuntimeException("La fecha de finalización no puede ser anterior a la fecha de inicio");
        }

        if (projectDTO.getCreatedById() == null) {
            throw new RuntimeException("Debe especificarse el ID del creador del proyecto");
        }

        User createdBy = userRepository.findById(projectDTO.getCreatedById())
                .orElseThrow(() -> new RuntimeException("Usuario creador no encontrado"));

        Project project = modelMapper.map(projectDTO, Project.class);
        project.setCreatedBy(createdBy);
        project.setStatus(ProjectStatus.IN_PROGRESS);

        Project savedProject = projectRepository.save(project);
        return modelMapper.map(savedProject, ProjectDTO.class);
    }


    public ProjectDTO getProjectById(Long id) {
        return projectRepository.findById(id)
                .map(project -> modelMapper.map(project, ProjectDTO.class))
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con id: " + id + ""));
    }

    public List<ProjectDTO> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(project -> modelMapper.map(project, ProjectDTO.class))
                .collect(Collectors.toList());
    }

    public ProjectDTO updateProject(Long id, ProjectDTO projectDTO) {
        return projectRepository.findById(id)
                .map(existingProject -> {
                    ProjectStatus originalStatus = existingProject.getStatus();
                    modelMapper.map(projectDTO, existingProject);
                    existingProject.setStatus(originalStatus);
                    Project updatedProject = projectRepository.save(existingProject);
                    return modelMapper.map(updatedProject, ProjectDTO.class);
                })
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
    }


    public ProjectDTO changeProjectStatus(Long id, ProjectStatus newStatus) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con id: " + id));

        validateStatusTransition(project.getStatus(), newStatus);

        project.setStatus(newStatus);

        if (newStatus == ProjectStatus.COMPLETED && project.getEndDate() == null) {
            project.setEndDate(LocalDate.now());
        }

        if (newStatus == ProjectStatus.CANCELED) {
            project.setEndDate(LocalDate.now());
        }

        Project updatedProject = projectRepository.save(project);
        return modelMapper.map(updatedProject, ProjectDTO.class);
    }

    private void validateStatusTransition(ProjectStatus currentStatus, ProjectStatus newStatus) {
        if (currentStatus == newStatus) {
            throw new IllegalStateException("El proyecto ya está en estado " + newStatus);
        }

        // No se puede volver a IN_PROGRESS desde COMPLETED o CANCELED
        if ((currentStatus == ProjectStatus.COMPLETED || currentStatus == ProjectStatus.CANCELED)
                && newStatus == ProjectStatus.IN_PROGRESS) {
            throw new IllegalStateException(
                    "No se puede reactivar un proyecto " + currentStatus.toString().toLowerCase());
        }

        // No se puede marcar como COMPLETED si está CANCELED
        if (currentStatus == ProjectStatus.CANCELED && newStatus == ProjectStatus.COMPLETED) {
            throw new IllegalStateException("No se puede finalizar un proyecto abandonado");
        }
    }


}


