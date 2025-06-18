package com.udea.fe.service;

import com.udea.fe.DTO.WorkspaceDTO;
import com.udea.fe.entity.Project;
import com.udea.fe.entity.Workspace;
import com.udea.fe.exception.ProjectNotFoundException;
import com.udea.fe.exception.WorkspaceNotFoundException;
import com.udea.fe.repository.ProjectRepository;
import com.udea.fe.repository.WorkspaceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WorkspaceServiceTest {

    private WorkspaceRepository workspaceRepository;
    private ProjectRepository projectRepository;
    private ModelMapper modelMapper;
    private WorkspaceService workspaceService;

    @BeforeEach
    void setUp() {
        workspaceRepository = mock(WorkspaceRepository.class);
        projectRepository = mock(ProjectRepository.class);
        modelMapper = mock(ModelMapper.class);
        workspaceService = new WorkspaceService(workspaceRepository, projectRepository, modelMapper);
    }

    @Test
    void createWorkspace_success() {
        WorkspaceDTO dto = new WorkspaceDTO();
        dto.setProjectId(1L);
        Workspace workspace = new Workspace();
        Project project = new Project();
        Workspace savedWorkspace = new Workspace();

        when(modelMapper.map(dto, Workspace.class)).thenReturn(workspace);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(workspaceRepository.save(workspace)).thenReturn(savedWorkspace);
        when(modelMapper.map(savedWorkspace, WorkspaceDTO.class)).thenReturn(dto);

        WorkspaceDTO result = workspaceService.createWorkspace(dto);

        assertNotNull(result);
        verify(workspaceRepository).save(workspace);
    }

    @Test
    void createWorkspace_projectNotFound_throwsException() {
        WorkspaceDTO dto = new WorkspaceDTO();
        dto.setProjectId(99L);

        when(modelMapper.map(dto, Workspace.class)).thenReturn(new Workspace());
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> workspaceService.createWorkspace(dto));
    }

    @Test
    void getWorkspaceById_success() {
        Workspace workspace = new Workspace();
        WorkspaceDTO dto = new WorkspaceDTO();

        when(workspaceRepository.findById(1L)).thenReturn(Optional.of(workspace));
        when(modelMapper.map(workspace, WorkspaceDTO.class)).thenReturn(dto);

        WorkspaceDTO result = workspaceService.getWorkspaceById(1L);

        assertNotNull(result);
    }

    @Test
    void getWorkspaceById_notFound_throwsException() {
        when(workspaceRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(WorkspaceNotFoundException.class, () -> workspaceService.getWorkspaceById(1L));
    }

    @Test
    void getAllWorkspaces_success() {
        Workspace workspace = new Workspace();
        WorkspaceDTO dto = new WorkspaceDTO();

        when(workspaceRepository.findAll()).thenReturn(List.of(workspace));
        when(modelMapper.map(workspace, WorkspaceDTO.class)).thenReturn(dto);

        List<WorkspaceDTO> result = workspaceService.getAllWorkspaces();
        assertEquals(1, result.size());
    }

    @Test
    void updateWorkspace_success() {
        WorkspaceDTO dto = new WorkspaceDTO();
        dto.setProjectId(1L);
        Workspace workspace = new Workspace();
        Project project = new Project();

        when(workspaceRepository.findById(1L)).thenReturn(Optional.of(workspace));
        doAnswer(invocation -> {
            WorkspaceDTO source = invocation.getArgument(0);
            Workspace dest = invocation.getArgument(1);
            return null;
        }).when(modelMapper).map(any(WorkspaceDTO.class), any(Workspace.class));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(workspaceRepository.save(workspace)).thenReturn(workspace);
        when(modelMapper.map(workspace, WorkspaceDTO.class)).thenReturn(dto);

        WorkspaceDTO result = workspaceService.updateWorkspace(1L, dto);

        assertNotNull(result);
    }

    @Test
    void updateWorkspace_notFound_throwsException() {
        WorkspaceDTO dto = new WorkspaceDTO();
        when(workspaceRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(WorkspaceNotFoundException.class, () -> workspaceService.updateWorkspace(1L, dto));
    }

    @Test
    void deleteWorkspace_success() {
        when(workspaceRepository.existsById(1L)).thenReturn(true);
        workspaceService.deleteWorkspace(1L);
        verify(workspaceRepository).deleteById(1L);
    }

    @Test
    void deleteWorkspace_notFound_throwsException() {
        when(workspaceRepository.existsById(1L)).thenReturn(false);
        assertThrows(WorkspaceNotFoundException.class, () -> workspaceService.deleteWorkspace(1L));
    }

    @Test
    void createWorkspace_projectNotFound_throwsException() {
        // Arrange
        WorkspaceDTO dto = new WorkspaceDTO();
        dto.setProjectId(999L); // Un ID que no existe

        when(projectRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        ProjectNotFoundException ex = assertThrows(
                ProjectNotFoundException.class,
                () -> workspaceService.createWorkspace(dto));
        assertEquals("Proyecto no encontrado con id: 999", ex.getMessage());
    }
}