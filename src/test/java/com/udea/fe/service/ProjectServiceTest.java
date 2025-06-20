package com.udea.fe.service;

import com.udea.fe.DTO.ProjectDTO;
import com.udea.fe.entity.Project;
import com.udea.fe.entity.ProjectStatus;
import com.udea.fe.entity.User;
import com.udea.fe.entity.UserTeam;
import com.udea.fe.exception.InvalidProjectDataException;
import com.udea.fe.exception.ProjectNotFoundException;
import com.udea.fe.exception.UserNotFoundException;
import com.udea.fe.mapper.ProjectMapper;
import com.udea.fe.repository.ProjectRepository;
import com.udea.fe.repository.UserRepository;
import com.udea.fe.repository.UserTeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProjectServiceTest {

    private ProjectRepository projectRepository;
    private UserRepository userRepository;
    private UserTeamRepository userTeamRepository;
    private ModelMapper modelMapper;
    private ProjectMapper projectMapper;
    private ProjectService projectService;

    @BeforeEach
    void setUp() {
        projectRepository = mock(ProjectRepository.class);
        userRepository = mock(UserRepository.class);
        userTeamRepository = mock(UserTeamRepository.class);
        modelMapper = mock(ModelMapper.class);
        projectMapper = mock(ProjectMapper.class);
        projectService = new ProjectService(projectRepository, userRepository, userTeamRepository, modelMapper, projectMapper);
    }

    @Test
    void createProject_success() {
        ProjectDTO dto = buildValidDTO();
        User user = new User();
        Project project = new Project();
        Project savedProject = new Project();
        ProjectDTO savedDTO = new ProjectDTO();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(modelMapper.map(dto, Project.class)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(savedProject);
        when(modelMapper.map(savedProject, ProjectDTO.class)).thenReturn(savedDTO);

        ProjectDTO result = projectService.createProject(dto);
        assertNotNull(result);
    }

    @Test
    void createProject_missingName_throws() {
        ProjectDTO dto = buildValidDTO();
        dto.setName(null);
        assertThrows(InvalidProjectDataException.class, () -> projectService.createProject(dto));
    }

    @Test
    void createProject_invalidDates_throws() {
        ProjectDTO dto = buildValidDTO();
        dto.setEndDate(LocalDate.now().minusDays(1));
        assertThrows(InvalidProjectDataException.class, () -> projectService.createProject(dto));
    }

    @Test
    void createProject_missingCreator_throws() {
        ProjectDTO dto = buildValidDTO();
        dto.setCreatedById(null);
        assertThrows(InvalidProjectDataException.class, () -> projectService.createProject(dto));
    }

    @Test
    void createProject_userNotFound_throws() {
        ProjectDTO dto = buildValidDTO();
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> projectService.createProject(dto));
    }

    @Test
    void getProjectById_found() {
        Project project = new Project();
        ProjectDTO dto = new ProjectDTO();
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(modelMapper.map(project, ProjectDTO.class)).thenReturn(dto);
        assertNotNull(projectService.getProjectById(1L));
    }

    @Test
    void getProjectById_notFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ProjectNotFoundException.class, () -> projectService.getProjectById(1L));
    }

    @Test
    void getAllProjects_returnsList() {
        Project project = new Project();
        ProjectDTO dto = new ProjectDTO();
        when(projectRepository.findAll()).thenReturn(List.of(project));
        when(modelMapper.map(project, ProjectDTO.class)).thenReturn(dto);
        assertEquals(1, projectService.getAllProjects().size());
    }

    @Test
    void updateProject_success_preservesStatus() {
        ProjectDTO dto = new ProjectDTO();
        Project project = new Project();
        project.setStatus(ProjectStatus.IN_PROGRESS);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(project)).thenReturn(project);
        when(modelMapper.map(project, ProjectDTO.class)).thenReturn(dto);

        ProjectDTO result = projectService.updateProject(1L, dto);
        assertEquals(dto, result);
        assertEquals(ProjectStatus.IN_PROGRESS, project.getStatus());
    }

    @Test
    void updateProject_notFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ProjectNotFoundException.class, () -> projectService.updateProject(1L, new ProjectDTO()));
    }

    @Test
    void changeProjectStatus_completed_setsEndDateIfNull() {
        Project project = new Project();
        project.setStatus(ProjectStatus.IN_PROGRESS);
        project.setEndDate(null);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(project)).thenReturn(project);
        when(modelMapper.map(project, ProjectDTO.class)).thenReturn(new ProjectDTO());
        projectService.changeProjectStatus(1L, ProjectStatus.COMPLETED);
        assertEquals(ProjectStatus.COMPLETED, project.getStatus());
        assertNotNull(project.getEndDate());
    }

    @Test
    void changeProjectStatus_sameStatus_throws() {
        Project project = new Project();
        project.setStatus(ProjectStatus.CANCELED);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        assertThrows(IllegalStateException.class, () -> projectService.changeProjectStatus(1L, ProjectStatus.CANCELED));
    }

    @Test
    void changeProjectStatus_invalidTransition_throws() {
        Project project = new Project();
        project.setStatus(ProjectStatus.CANCELED);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        assertThrows(IllegalStateException.class, () -> projectService.changeProjectStatus(1L, ProjectStatus.IN_PROGRESS));
    }

    @Test
    void getProjectsByUserId_returnsCombinedList() {
        UserTeam userTeam = mock(UserTeam.class);
        Project teamProject = new Project();
        Project createdProject = new Project();
        ProjectDTO dto1 = new ProjectDTO();
        ProjectDTO dto2 = new ProjectDTO();

        when(userTeam.getTeam()).thenReturn(mock(com.udea.fe.entity.Team.class));
        when(userTeam.getTeam().getProject()).thenReturn(teamProject);
        when(userTeamRepository.findByIdUserId(1L)).thenReturn(List.of(userTeam));
        when(projectRepository.findByCreatedByUserId(1L)).thenReturn(List.of(createdProject));
        when(projectMapper.toDTO(teamProject)).thenReturn(dto1);
        when(projectMapper.toDTO(createdProject)).thenReturn(dto2);

        List<ProjectDTO> result = projectService.getProjectsByUserId(1L);
        assertEquals(2, result.size());
    }

    private ProjectDTO buildValidDTO() {
        ProjectDTO dto = new ProjectDTO();
        dto.setName("Proyecto");
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now().plusDays(1));
        dto.setCreatedById(1L);
        return dto;
    }
}
