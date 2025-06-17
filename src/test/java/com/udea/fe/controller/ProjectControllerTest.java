package com.udea.fe.controller;

import com.udea.fe.DTO.ProjectDTO;
import com.udea.fe.service.ProjectService;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

public class ProjectControllerTest {

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ProjectController projectController;

    public ProjectControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetMyProjects() {
        Long userId = 1L;

        ProjectDTO project1 = new ProjectDTO();
        project1.setName("Proyecto 1");

        ProjectDTO project2 = new ProjectDTO();
        project2.setName("Proyecto 2");

        List<ProjectDTO> mockProjects = Arrays.asList(project1, project2);

        when(projectService.getProjectsByUserId(userId)).thenReturn(mockProjects);

        ResponseEntity<List<ProjectDTO>> response = projectController.getMyProjects(userId);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("Proyecto 1", response.getBody().get(0).getName());
    }
}
