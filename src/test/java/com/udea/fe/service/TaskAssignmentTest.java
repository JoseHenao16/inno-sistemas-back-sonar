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
        service = new TaskAssignmentService(taskRepository, taskAssignmentRepository, userRepository, notificationService);
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
        TaskAssignmentId id1 = new TaskAssignmentId();
        TaskAssignmentId id2 = new TaskAssignmentId();
        TaskAssignmentId id3 = new TaskAssignmentId();

        TaskAssignment a1 = new TaskAssignment(); a1.setId(id1);
        TaskAssignment a2 = new TaskAssignment(); a2.setId(id2);
        TaskAssignment a3 = new TaskAssignment(); a3.setId(id3);

        User u10 = new User(); u10.setUserId(10L);
        User u11 = new User(); u11.setUserId(11L);

        when(taskAssignmentRepository.findById_TaskId(1L)).thenReturn(List.of(a1, a2, a3));
        when(userRepository.findById(10L)).thenReturn(Optional.of(u10));
        when(userRepository.findById(11L)).thenReturn(Optional.of(u11));

        List<User> result = service.getUsersAssignedToTask(1L);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(u -> u.getUserId().equals(10L)));
        assertTrue(result.stream().anyMatch(u -> u.getUserId().equals(11L)));
    }

    @Test
    void getUsersAssignedToTask_someUsersMissing_returnsOnlyFound() {
        TaskAssignmentId id1 = new TaskAssignmentId();
        TaskAssignmentId id2 = new TaskAssignmentId();

        TaskAssignment a1 = new TaskAssignment(); a1.setId(id1);
        TaskAssignment a2 = new TaskAssignment(); a2.setId(id2);

        User u10 = new User(); u10.setUserId(10L);

        when(taskAssignmentRepository.findById_TaskId(1L)).thenReturn(List.of(a1, a2));
        when(userRepository.findById(10L)).thenReturn(Optional.of(u10));
        when(userRepository.findById(11L)).thenReturn(Optional.empty());

        List<User> result = service.getUsersAssignedToTask(1L);

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getUserId());
    }
}