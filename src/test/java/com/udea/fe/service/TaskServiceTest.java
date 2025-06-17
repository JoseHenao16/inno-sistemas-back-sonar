package com.udea.fe.service;

import com.udea.fe.DTO.TaskDTO;
import com.udea.fe.entity.*;
import com.udea.fe.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class TaskServiceTest {

    @InjectMocks
    private TaskService taskService;

    @Mock private TaskRepository taskRepository;
    @Mock private TaskAssignmentRepository taskAssignmentRepository;
    @Mock private ProjectRepository projectRepository;
    @Mock private UserRepository userRepository;
    @Mock private UserTeamRepository userTeamRepository;
    @Mock private ModelMapper modelMapper;

    private TaskDTO taskDTO;
    private Task task;
    private Project project;
    private User user;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setProjectId(1L);

        user = new User();
        user.setUserId(1L);
        user.setRole(Role.STUDENT);

        taskDTO = new TaskDTO();
        taskDTO.setName("Test Task");
        taskDTO.setDescription("Description");
        taskDTO.setDueDate(LocalDateTime.now().plusDays(1));
        taskDTO.setPriority(TaskPriority.HIGH);
        taskDTO.setProjectId(1L);
        taskDTO.setCreatedById(1L);

        task = new Task();
        task.setTaskId(1L);
        task.setName("Test Task");
        task.setProject(project);
        task.setCreatedBy(user);
        task.setStatus(TaskStatus.PENDING);
    }

    @Test
    void createTask_successful() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(taskRepository.existsByNameAndProject_ProjectId("Test Task", 1L)).thenReturn(false);
        when(modelMapper.map(taskDTO, Task.class)).thenReturn(task);
        when(taskRepository.save(any())).thenReturn(task);
        when(modelMapper.map(task, TaskDTO.class)).thenReturn(taskDTO);

        TaskDTO result = taskService.createTask(taskDTO);
        assertNotNull(result);
        assertEquals("Test Task", result.getName());
    }

    @Test
    void createTask_throwsIfMissingFields() {
        taskDTO.setName(null);
        Exception ex = assertThrows(IllegalArgumentException.class, () -> taskService.createTask(taskDTO));
        assertEquals("El nombre de la tarea es obligatorio", ex.getMessage());
    }

    @Test
    void getTaskById_successful() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(modelMapper.map(task, TaskDTO.class)).thenReturn(taskDTO);

        TaskDTO result = taskService.getTaskById(1L);
        assertEquals(taskDTO.getName(), result.getName());
    }

    @Test
    void getTaskById_notFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> taskService.getTaskById(1L));
    }

    @Test
    void updateTask_successful() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(taskRepository.save(task)).thenReturn(task);
        when(modelMapper.map(task, TaskDTO.class)).thenReturn(taskDTO);

        TaskDTO result = taskService.updateTask(1L, taskDTO);
        assertNotNull(result);
    }

    @Test
    void updateTask_throwsIfStatusChanged() {
        task.setStatus(TaskStatus.PENDING);
        taskDTO.setStatus(TaskStatus.IN_PROGRESS);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        assertThrows(IllegalArgumentException.class, () -> taskService.updateTask(1L, taskDTO));
        }

        @Test
        void updateTaskStatus_successful() {
        task.setStatus(TaskStatus.PENDING);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);
        when(modelMapper.map(task, TaskDTO.class)).thenReturn(taskDTO);

        TaskDTO result = taskService.updateTaskStatus(1L, TaskStatus.IN_PROGRESS);
        assertNotNull(result);
        }

        @Test
        void updateTaskStatus_invalidTransition() {
        task.setStatus(TaskStatus.COMPLETED);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        assertThrows(IllegalStateException.class, () -> taskService.updateTaskStatus(1L, TaskStatus.PENDING));
        }

        @Test
        void updateTaskStatus_nullStatus() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        Exception ex = assertThrows(IllegalArgumentException.class, () -> taskService.updateTaskStatus(1L, null));
        assertEquals("El estado no puede ser nulo", ex.getMessage());
        }

        @Test
        void updateTaskStatus_sameStatus() {
        task.setStatus(TaskStatus.PENDING);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        Exception ex = assertThrows(IllegalArgumentException.class, () -> taskService.updateTaskStatus(1L, TaskStatus.PENDING));
        assertEquals("La tarea ya tiene este estado", ex.getMessage());
        }

        @Test
        void updateTaskStatus_taskNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> taskService.updateTaskStatus(1L, TaskStatus.PENDING));
        }

        @Test
        void getTasksByProjectIdAndUser_asTeacher() {
        user.setRole(Role.TEACHER);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userTeamRepository.existsByUserIdAndProjectId(1L, 1L)).thenReturn(true);
        when(taskRepository.findByProject_ProjectId(1L)).thenReturn(List.of(task));
        when(modelMapper.map(task, TaskDTO.class)).thenReturn(taskDTO);

        List<TaskDTO> result = taskService.getTasksByProjectIdAndUser(1L, "test@example.com");
        assertEquals(1, result.size());
        assertEquals("Test Task", result.get(0).getName());
        }

        @Test
        void getTasksByProjectIdAndUser_asStudentAssigned() {
        user.setRole(Role.STUDENT);
        when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(user));
        when(userTeamRepository.existsByUserIdAndProjectId(1L, 1L)).thenReturn(false);
        TaskAssignment assignment = mock(TaskAssignment.class);
        when(assignment.getTask()).thenReturn(task);
        when(taskAssignmentRepository.findById_AssignedIdAndId_AssignedType(1L, "USER")).thenReturn(List.of(assignment));
        when(taskRepository.findByProject_ProjectId(1L)).thenReturn(List.of(task));
        when(modelMapper.map(task, TaskDTO.class)).thenReturn(taskDTO);

        List<TaskDTO> result = taskService.getTasksByProjectIdAndUser(1L, "student@example.com");
        assertEquals(1, result.size());
        assertEquals("Test Task", result.get(0).getName());
        }

        @Test
        void getTasksByProjectIdAndUser_userNotFound() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> taskService.getTasksByProjectIdAndUser(1L, "notfound@example.com"));
        }

        @Test
        void getAllTasks_successful() {
        when(taskRepository.findAll()).thenReturn(List.of(task));
        when(modelMapper.map(task, TaskDTO.class)).thenReturn(taskDTO);

        List<TaskDTO> result = taskService.getAllTasks();
        assertEquals(1, result.size());
        assertEquals("Test Task", result.get(0).getName());
        }

        @Test
        void createTask_throwsIfDescriptionMissing() {
        taskDTO.setDescription(null);
        Exception ex = assertThrows(IllegalArgumentException.class, () -> taskService.createTask(taskDTO));
        assertEquals("La descripción de la tarea es obligatoria", ex.getMessage());
        }

        @Test
        void createTask_throwsIfDueDateMissing() {
        taskDTO.setDueDate(null);
        Exception ex = assertThrows(IllegalArgumentException.class, () -> taskService.createTask(taskDTO));
        assertEquals("La fecha de vencimiento es obligatoria", ex.getMessage());
        }

        @Test
        void createTask_throwsIfDueDateInPast() {
        taskDTO.setDueDate(LocalDateTime.now().minusDays(1));
        Exception ex = assertThrows(IllegalArgumentException.class, () -> taskService.createTask(taskDTO));
        assertEquals("La fecha de vencimiento no puede ser anterior a la fecha actual", ex.getMessage());
        }

        @Test
        void createTask_throwsIfPriorityMissing() {
        taskDTO.setPriority(null);
        Exception ex = assertThrows(IllegalArgumentException.class, () -> taskService.createTask(taskDTO));
        assertEquals("La prioridad de la tarea es obligatoria", ex.getMessage());
        }

        @Test
        void createTask_throwsIfProjectNotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());
        Exception ex = assertThrows(RuntimeException.class, () -> taskService.createTask(taskDTO));
        assertEquals("Proyecto no encontrado", ex.getMessage());
        }

        @Test
        void createTask_throwsIfUserNotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        Exception ex = assertThrows(RuntimeException.class, () -> taskService.createTask(taskDTO));
        assertEquals("Usuario no encontrado", ex.getMessage());
        }

        @Test
        void createTask_throwsIfTaskExists() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(taskRepository.existsByNameAndProject_ProjectId("Test Task", 1L)).thenReturn(true);
        Exception ex = assertThrows(IllegalArgumentException.class, () -> taskService.createTask(taskDTO));
        assertEquals("Ya existe una tarea con ese nombre en este proyecto", ex.getMessage());
        }

        @Test
        void updateTask_notFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> taskService.updateTask(1L, taskDTO));
        }

        @Test
        void updateTask_projectNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        taskDTO.setProjectId(2L);
        when(projectRepository.findById(2L)).thenReturn(Optional.empty());
        Exception ex = assertThrows(RuntimeException.class, () -> taskService.updateTask(1L, taskDTO));
        assertEquals("Proyecto no encontrado", ex.getMessage());
        }

        @Test
        void updateTask_userNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        taskDTO.setCreatedById(2L);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        Exception ex = assertThrows(RuntimeException.class, () -> taskService.updateTask(1L, taskDTO));
        assertEquals("Usuario no encontrado", ex.getMessage());
        }
    }