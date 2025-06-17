package com.udea.fe.controller;

import com.udea.fe.DTO.TaskDTO;
import com.udea.fe.entity.TaskStatus;
import com.udea.fe.service.TaskService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTask() {
        TaskDTO taskDTO = new TaskDTO();
        when(taskService.createTask(taskDTO)).thenReturn(taskDTO);

        ResponseEntity<TaskDTO> response = taskController.createTask(taskDTO);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(taskDTO, response.getBody());
    }

    @Test
    void testGetTaskById() {
        Long id = 1L;
        TaskDTO taskDTO = new TaskDTO();
        when(taskService.getTaskById(id)).thenReturn(taskDTO);

        ResponseEntity<TaskDTO> response = taskController.getTaskById(id);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(taskDTO, response.getBody());
    }

    @Test
    void testGetAllTasks() {
        List<TaskDTO> taskList = Arrays.asList(new TaskDTO(), new TaskDTO());
        when(taskService.getAllTasks()).thenReturn(taskList);

        ResponseEntity<List<TaskDTO>> response = taskController.getAllTasks();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(taskList, response.getBody());
    }

    @Test
    void testUpdateTask() {
        Long id = 1L;
        TaskDTO taskDTO = new TaskDTO();
        when(taskService.updateTask(id, taskDTO)).thenReturn(taskDTO);

        ResponseEntity<TaskDTO> response = taskController.updateTask(id, taskDTO);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(taskDTO, response.getBody());
    }

    @Test
    void testUpdateTaskStatus() {
        Long id = 1L;
        TaskStatus status = TaskStatus.COMPLETED;
        TaskDTO taskDTO = new TaskDTO();

        when(taskService.updateTaskStatus(id, status)).thenReturn(taskDTO);

        ResponseEntity<TaskDTO> response = taskController.updateTaskStatus(id, status);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(taskDTO, response.getBody());
    }

    @Test
    void testGetTasksByProject() {
        Long projectId = 1L;
        Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("test@example.com");

        List<TaskDTO> taskList = Arrays.asList(new TaskDTO(), new TaskDTO());
        when(taskService.getTasksByProjectIdAndUser(projectId, "test@example.com")).thenReturn(taskList);

        ResponseEntity<List<TaskDTO>> response = taskController.getTasksByProject(projectId, mockPrincipal);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(taskList, response.getBody());
    }
}

