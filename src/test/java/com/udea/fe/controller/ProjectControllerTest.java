package com.udea.fe.controller;

import com.udea.fe.DTO.ProjectDTO;
import com.udea.fe.entity.ProjectStatus;
import com.udea.fe.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProjectControllerTest {

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ProjectController projectController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetMyProjects() {
        Long userId = 1L;
        ProjectDTO p1 = new ProjectDTO(); p1.setName("Proyecto 1");
        ProjectDTO p2 = new ProjectDTO(); p2.setName("Proyecto 2");
        when(projectService.getProjectsByUserId(userId)).thenReturn(Arrays.asList(p1, p2));

        ResponseEntity<List<ProjectDTO>> response = projectController.getMyProjects(userId);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
    }

    @Test
    public void testGetMyProjects_exception() {
        Long userId = 99L;
        when(projectService.getProjectsByUserId(userId)).thenThrow(new RuntimeException("Error"));

        ResponseEntity<List<ProjectDTO>> response = projectController.getMyProjects(userId);
        assertEquals(500, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    public void testCreateProject() {
        ProjectDTO dto = new ProjectDTO(); dto.setName("Nuevo Proyecto");
        when(projectService.createProject(dto)).thenReturn(dto);

        ResponseEntity<ProjectDTO> response = projectController.createProject(dto);
        assertEquals(201, response.getStatusCodeValue());
        assertEquals("Nuevo Proyecto", response.getBody().getName());
    }

    @Test
    public void testGetProjectById() {
        ProjectDTO dto = new ProjectDTO(); dto.setName("Proyecto 1");
        when(projectService.getProjectById(1L)).thenReturn(dto);

        ResponseEntity<ProjectDTO> response = projectController.getProjectById(1L);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Proyecto 1", response.getBody().getName());
    }

    @Test
    public void testGetAllProjects() {
        ProjectDTO dto = new ProjectDTO(); dto.setName("Proyecto");
        when(projectService.getAllProjects()).thenReturn(Collections.singletonList(dto));

        ResponseEntity<List<ProjectDTO>> response = projectController.getAllProjects();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void testUpdateProject() {
        ProjectDTO dto = new ProjectDTO(); dto.setName("Editado");
        when(projectService.updateProject(1L, dto)).thenReturn(dto);

        ResponseEntity<ProjectDTO> response = projectController.updateProject(1L, dto);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Editado", response.getBody().getName());
    }

    @Test
    public void testUpdateProjectStatus() {
        ProjectDTO dto = new ProjectDTO(); dto.setStatus(ProjectStatus.COMPLETED);
        when(projectService.changeProjectStatus(1L, ProjectStatus.COMPLETED)).thenReturn(dto);

        ResponseEntity<ProjectDTO> response = projectController.updateProjectStatus(1L, ProjectStatus.COMPLETED);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(ProjectStatus.COMPLETED, response.getBody().getStatus());
    }
} 
