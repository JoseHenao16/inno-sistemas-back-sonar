package com.udea.fe.controller;

import com.udea.fe.DTO.WorkspaceDTO;
import com.udea.fe.service.WorkspaceService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WorkspaceControllerTest {

    @Mock
    private WorkspaceService workspaceService;

    @InjectMocks
    private WorkspaceController workspaceController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateWorkspace() {
        WorkspaceDTO dto = new WorkspaceDTO();
        when(workspaceService.createWorkspace(dto)).thenReturn(dto);

        ResponseEntity<WorkspaceDTO> response = workspaceController.createWorkspace(dto);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(dto, response.getBody());
    }

    @Test
    void testGetWorkspaceById() {
        Long id = 1L;
        WorkspaceDTO dto = new WorkspaceDTO();
        when(workspaceService.getWorkspaceById(id)).thenReturn(dto);

        ResponseEntity<WorkspaceDTO> response = workspaceController.getWorkspaceById(id);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(dto, response.getBody());
    }

    @Test
    void testGetAllWorkspaces() {
        List<WorkspaceDTO> list = Arrays.asList(new WorkspaceDTO(), new WorkspaceDTO());
        when(workspaceService.getAllWorkspaces()).thenReturn(list);

        ResponseEntity<List<WorkspaceDTO>> response = workspaceController.getAllWorkspaces();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(list, response.getBody());
    }

    @Test
    void testUpdateWorkspace() {
        Long id = 1L;
        WorkspaceDTO dto = new WorkspaceDTO();
        when(workspaceService.updateWorkspace(id, dto)).thenReturn(dto);

        ResponseEntity<WorkspaceDTO> response = workspaceController.updateWorkspace(id, dto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(dto, response.getBody());
    }

    @Test
    void testDeleteWorkspace() {
        Long id = 1L;
        doNothing().when(workspaceService).deleteWorkspace(id);

        ResponseEntity<Void> response = workspaceController.deleteWorkspace(id);

        assertEquals(204, response.getStatusCodeValue());
        assertNull(response.getBody());
    }
}

