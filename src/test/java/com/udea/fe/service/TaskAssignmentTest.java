package com.udea.fe.service;

import com.udea.fe.DTO.NotificationDTO;
import com.udea.fe.DTO.TaskAssignmentRequestDTO;
import com.udea.fe.DTO.TaskAssignmentResponseDTO;
import com.udea.fe.entity.*;
import com.udea.fe.repository.TaskAssignmentRepository;
import com.udea.fe.repository.TaskRepository;
import com.udea.fe.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskAssignmentServiceTest {

    private TaskRepository taskRepository;
    private TaskAssignmentRepository taskAssignmentRepository;
    private UserRepository userRepository;
    private NotificationService notificationService;
    private TaskAssignmentService service;

    @BeforeEach
    void setUp() {
        taskRepository = mock(TaskRepository.class);
        taskAssignmentRepository = mock(TaskAssignmentRepository.class);
        userRepository = mock(UserRepository.class);
        notificationService = mock(NotificationService.class);
        service = new TaskAssignmentService(taskRepository, taskAssignmentRepository, userRepository,
                notificationService);
    }

    @Test
    void assignTask_success() {
        TaskAssignmentRequestDTO request = new TaskAssignmentRequestDTO();
        request.setTaskId(1L);
        request.setAssignedId(2L);
        request.setAssignedType("USER");

        Task task = new Task();
        task.setTaskId(1L);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        TaskAssignmentResponseDTO response = service.assignTask(request);

        assertEquals(1L, response.getTaskId());
        assertEquals(2L, response.getAssignedId());
        assertEquals("USER", response.getAssignedType());
        assertEquals("Tarea asignada correctamente", response.getMessage());

        verify(taskAssignmentRepository).save(any(TaskAssignment.class));
        verify(notificationService).createNotification(any(NotificationDTO.class));
    }

    @Test
    void assignTask_taskNotFound_throwsException() {
        TaskAssignmentRequestDTO request = new TaskAssignmentRequestDTO();
        request.setTaskId(1L);

        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(RuntimeException.class, () -> service.assignTask(request));
        assertEquals("Tarea no encontrada", ex.getMessage());
    }

    @Test
    void getUsersAssignedToTask_success() {
        Long taskId = 1L;

        TaskAssignmentId id1 = new TaskAssignmentId();
        id1.setTaskId(taskId);
        id1.setAssignedId(1L);
        id1.setAssignedType("USER");

        TaskAssignmentId id2 = new TaskAssignmentId();
        id2.setTaskId(taskId);
        id2.setAssignedId(2L);
        id2.setAssignedType("USER");

        TaskAssignment assignment1 = new TaskAssignment();
        assignment1.setId(id1);

        TaskAssignment assignment2 = new TaskAssignment();
        assignment2.setId(id2);

        User user1 = new User();
        user1.setUserId(1L);

        User user2 = new User();
        user2.setUserId(2L);

        when(taskAssignmentRepository.findById_TaskId(taskId))
                .thenReturn(List.of(assignment1, assignment2));

        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(user1));
        when(userRepository.findById(eq(2L))).thenReturn(Optional.of(user2));

        List<User> result = service.getUsersAssignedToTask(taskId);

        assertEquals(2, result.size());
    }

    @Test
    void getUsersAssignedToTask_someUsersMissing_returnsOnlyFound() {
        Long taskId = 1L;

        TaskAssignmentId id1 = new TaskAssignmentId();
        id1.setTaskId(taskId);
        id1.setAssignedId(1L);
        id1.setAssignedType("USER");

        TaskAssignmentId id2 = new TaskAssignmentId();
        id2.setTaskId(taskId);
        id2.setAssignedId(2L);
        id2.setAssignedType("USER");

        TaskAssignment assignment1 = new TaskAssignment();
        assignment1.setId(id1);

        TaskAssignment assignment2 = new TaskAssignment();
        assignment2.setId(id2);

        User user1 = new User();
        user1.setUserId(1L);

        when(taskAssignmentRepository.findById_TaskId(taskId))
                .thenReturn(List.of(assignment1, assignment2));

        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(user1));
        when(userRepository.findById(eq(2L))).thenReturn(Optional.empty()); // simulamos uno que no existe

        List<User> result = service.getUsersAssignedToTask(taskId);

        assertEquals(1, result.size());
    }
}