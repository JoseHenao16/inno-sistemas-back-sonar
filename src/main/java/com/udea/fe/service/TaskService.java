package com.udea.fe.service;

import com.udea.fe.DTO.TaskDTO;
import com.udea.fe.entity.Project;
import com.udea.fe.entity.Role;
import com.udea.fe.entity.Task;
import com.udea.fe.entity.TaskStatus;
import com.udea.fe.entity.User;
import com.udea.fe.repository.ProjectRepository;
import com.udea.fe.repository.TaskAssignmentRepository;
import com.udea.fe.repository.TaskRepository;
import com.udea.fe.repository.UserRepository;
import com.udea.fe.repository.UserTeamRepository;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@Transactional
@AllArgsConstructor
public class TaskService {

  private final TaskRepository taskRepository;
  private final TaskAssignmentRepository taskAssignmentRepository;
  private final ProjectRepository projectRepository;
  private final UserRepository userRepository;
  private final UserTeamRepository userTeamRepository;
  private final ModelMapper modelMapper;
  

  public TaskDTO createTask(TaskDTO taskDTO) {
    if (taskDTO.getName() == null || taskDTO.getName().isBlank()) {
      throw new IllegalArgumentException(
        "El nombre de la tarea es obligatorio"
      );
    }

    if (
      taskDTO.getDescription() == null || taskDTO.getDescription().isBlank()
    ) {
      throw new IllegalArgumentException(
        "La descripción de la tarea es obligatoria"
      );
    }

    if (taskDTO.getDueDate() == null) {
      throw new IllegalArgumentException(
        "La fecha de vencimiento es obligatoria"
      );
    }

    if (taskDTO.getDueDate().isBefore(LocalDateTime.now())) {
      throw new IllegalArgumentException(
        "La fecha de vencimiento no puede ser anterior a la fecha actual"
      );
    }

    if (taskDTO.getPriority() == null) {
      throw new IllegalArgumentException(
        "La prioridad de la tarea es obligatoria"
      );
    }

    Project project = projectRepository
      .findById(taskDTO.getProjectId())
      .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

    User createdBy = userRepository
      .findById(taskDTO.getCreatedById())
      .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    // Validar si ya existe una tarea con el mismo nombre en el mismo proyecto
    boolean taskExists = taskRepository.existsByNameAndProject_ProjectId(
      taskDTO.getName(),
      taskDTO.getProjectId()
    );
    if (taskExists) {
      throw new IllegalArgumentException(
        "Ya existe una tarea con ese nombre en este proyecto"
      );
    }

    Task task = modelMapper.map(taskDTO, Task.class);
    task.setProject(project);
    task.setCreatedBy(createdBy);
    task.setStatus(TaskStatus.PENDING);
    task.setCreatedAt(LocalDateTime.now());

    Task savedTask = taskRepository.save(task);
    return modelMapper.map(savedTask, TaskDTO.class);
  }

  public TaskDTO getTaskById(Long id) {
    return taskRepository
      .findById(id)
      .map(task -> modelMapper.map(task, TaskDTO.class))
      .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));
  }

  public List<TaskDTO> getAllTasks() {
    return taskRepository
      .findAll()
      .stream()
      .map(task -> modelMapper.map(task, TaskDTO.class))
      .collect(Collectors.toList());
  }

  public TaskDTO updateTask(Long id, TaskDTO taskDTO) {
    return taskRepository
      .findById(id)
      .map(task -> {
        modelMapper.map(taskDTO, task);

        // No permitir cambiar el estado desde aquí
        if (
          taskDTO.getStatus() != null && task.getStatus() != taskDTO.getStatus()
        ) {
          throw new IllegalArgumentException(
            "El estado de la tarea no puede modificarse desde esta función"
          );
        }

        if (taskDTO.getProjectId() != null) {
          Project project = projectRepository
            .findById(taskDTO.getProjectId())
            .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
          task.setProject(project);
        }

        if (taskDTO.getCreatedById() != null) {
          User user = userRepository
            .findById(taskDTO.getCreatedById())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
          task.setCreatedBy(user);
        }

        Task updatedTask = taskRepository.save(task);
        return modelMapper.map(updatedTask, TaskDTO.class);
      })
      .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));
  }

  public TaskDTO updateTaskStatus(Long id, TaskStatus status) {
    if (status == null) {
      throw new IllegalArgumentException("El estado no puede ser nulo");
    }

    return taskRepository
      .findById(id)
      .map(task -> {
        if (task.getStatus() == status) {
          throw new IllegalArgumentException("La tarea ya tiene este estado");
        }

        if (task.getStatus() == TaskStatus.COMPLETED) {
          throw new IllegalStateException(
            "No se puede cambiar el estado de una tarea finalizada"
          );
        }

        task.setStatus(status);
        Task updatedTask = taskRepository.save(task);
        return modelMapper.map(updatedTask, TaskDTO.class);
      })
      .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));
  }

  public List<TaskDTO> getTasksByProjectIdAndUser(Long projectId, String userEmail) {
    User user = userRepository.findByEmail(userEmail)
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    // Verifica si el usuario es profesor y está en el grupo del proyecto
    boolean isTeacher = user.getRole() == Role.TEACHER;

    System.out.println("\n isTeacher?" + isTeacher);
    boolean isInProjectTeam = userTeamRepository.existsByUserIdAndProjectId(user.getUserId(), projectId);
    System.out.println("\n isInProjectTeam?" + isInProjectTeam);

    if (isTeacher && isInProjectTeam) {
        return taskRepository.findByProject_ProjectId(projectId)
            .stream().map(task -> modelMapper.map(task, TaskDTO.class)).collect(Collectors.toList());
    } else {
        // Solo tareas asignadas al usuario
        List<Long> assignedTaskIds = taskAssignmentRepository.findById_AssignedIdAndId_AssignedType(user.getUserId(), "USER")
            .stream().map(a -> a.getTask().getTaskId()).collect(Collectors.toList());

        return taskRepository.findByProject_ProjectId(projectId)
            .stream().filter(task -> assignedTaskIds.contains(task.getTaskId()))
            .map(task -> modelMapper.map(task, TaskDTO.class)).collect(Collectors.toList());
    }
}
}
