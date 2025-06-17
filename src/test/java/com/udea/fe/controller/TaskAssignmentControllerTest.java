package com.udea.fe.controller;

import com.udea.fe.DTO.TaskAssignmentRequestDTO;
import com.udea.fe.DTO.TaskAssignmentResponseDTO;
import com.udea.fe.entity.User;
import com.udea.fe.service.TaskAssignmentService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskAssignmentControllerTest {

    @Mock
    private TaskAssignmentService taskAssignmentService;

    @InjectMocks
    private TaskAssignmentController taskAssignmentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAssignTaskSuccess() {
        TaskAssignmentRequestDTO request = new TaskAssignmentRequestDTO();
        TaskAssignmentResponseDTO response = new TaskAssignmentResponseDTO();

        when(taskAssignmentService.assignTask(request)).thenReturn(response);

        ResponseEntity<TaskAssignmentResponseDTO> result = taskAssignmentController.assignTask(request);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(response, result.getBody());
    }

    @Test
    void testAssignTaskFailure() {
        TaskAssignmentRequestDTO request = new TaskAssignmentRequestDTO();

        when(taskAssignmentService.assignTask(request)).thenThrow(new RuntimeException("Error"));

        ResponseEntity<TaskAssignmentResponseDTO> result = taskAssignmentController.assignTask(request);

        assertEquals(400, result.getStatusCodeValue());
    }

    @Test
    void testGetUsersAssignedToTaskSuccess() {
        Long taskId = 1L;
        List<User> mockUsers = Arrays.asList(new User(), new User());

        when(taskAssignmentService.getUsersAssignedToTask(taskId)).thenReturn(mockUsers);

        ResponseEntity<List<User>> result = taskAssignmentController.getUsersAssignedToTask(taskId);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(mockUsers, result.getBody());
    }

    @Test
    void testGetUsersAssignedToTaskFailure() {
        Long taskId = 1L;

        when(taskAssignmentService.getUsersAssignedToTask(taskId)).thenThrow(new RuntimeException("Error"));

        ResponseEntity<List<User>> result = taskAssignmentController.getUsersAssignedToTask(taskId);

        assertEquals(400, result.getStatusCodeValue());
    }
}
