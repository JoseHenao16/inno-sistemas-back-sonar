package com.udea.fe.service;

import com.udea.fe.DTO.TeamDTO;
import com.udea.fe.entity.*;
import com.udea.fe.exception.*;
import com.udea.fe.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class TeamServiceTest {

    private TeamRepository teamRepository;
    private ProjectRepository projectRepository;
    private UserRepository userRepository;
    private UserTeamRepository userTeamRepository;
    private ModelMapper modelMapper;
    private TeamService teamService;

    @BeforeEach
    void setUp() {
        teamRepository = mock(TeamRepository.class);
        projectRepository = mock(ProjectRepository.class);
        userRepository = mock(UserRepository.class);
        userTeamRepository = mock(UserTeamRepository.class);
        modelMapper = mock(ModelMapper.class);
        teamService = new TeamService(teamRepository, projectRepository, userRepository, userTeamRepository,
                modelMapper);
    }

    @Test
    void createTeam_success() {
        TeamDTO dto = new TeamDTO();
        dto.setName("Team 1");
        dto.setProjectId(1L);
        dto.setLeaderId(2L);

        Team team = new Team();
        team.setName("Team 1");

        Project project = new Project();
        project.setProjectId(1L);

        User leader = new User();
        leader.setUserId(2L);

        when(modelMapper.map(dto, Team.class)).thenReturn(team);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userRepository.findById(2L)).thenReturn(Optional.of(leader));
        when(teamRepository.save(team)).thenReturn(team);
        when(modelMapper.map(team, TeamDTO.class)).thenReturn(dto);

        TeamDTO result = teamService.createTeam(dto);

        assertEquals("Team 1", result.getName());
        verify(teamRepository).save(team);
    }

    @Test
    void createTeam_projectNotFound() {
        TeamDTO dto = new TeamDTO();
        dto.setProjectId(999L);

        when(projectRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> teamService.createTeam(dto));
    }

    @Test
    void addUserToTeam_success() {
        Long userId = 1L;
        Long teamId = 2L;
        String role = "Colaborador";

        User user = new User();
        user.setUserId(userId);
        Team team = new Team();
        team.setTeamId(teamId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(userTeamRepository.existsById(new UserTeamId(userId, teamId))).thenReturn(false);

        teamService.addUserToTeam(userId, teamId, role);

        verify(userTeamRepository).save(any(UserTeam.class));
    }

    @Test
    void removeUserFromTeam_success() {
        Long userId = 1L;
        Long teamId = 2L;
        UserTeamId id = new UserTeamId(userId, teamId);

        // Simular que el usuario sí está en el equipo
        when(userTeamRepository.existsById(any(UserTeamId.class))).thenReturn(true);
        doNothing().when(userTeamRepository).deleteById(any(UserTeamId.class));

        teamService.removeUserFromTeam(userId, teamId);

        // Verificar que se llamó deleteById con algún UserTeamId
        verify(userTeamRepository).deleteById(any(UserTeamId.class));
    }

    @Test
    void removeUserFromTeam_notInTeam() {
        Long userId = 1L;
        Long teamId = 2L;

        when(userTeamRepository.existsById(new UserTeamId(userId, teamId))).thenReturn(false);

        assertThrows(NotInTeamException.class, () -> teamService.removeUserFromTeam(userId, teamId));
    }

    @Test
    void getTeamById_success() {
        Team team = new Team();
        team.setTeamId(1L);
        TeamDTO dto = new TeamDTO();
        dto.setId(1L);

        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(modelMapper.map(team, TeamDTO.class)).thenReturn(dto);

        TeamDTO result = teamService.getTeamById(1L);
        assertEquals(1L, result.getId());
    }

    @Test
    void getTeamsByProject_success() {
        Project project = new Project();
        Team team1 = new Team();
        Team team2 = new Team();
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(teamRepository.findByProject(project)).thenReturn(List.of(team1, team2));
        when(modelMapper.map(any(Team.class), eq(TeamDTO.class)))
                .thenReturn(new TeamDTO(), new TeamDTO());

        List<TeamDTO> result = teamService.getTeamsByProject(1L);

        assertEquals(2, result.size());
    }

    @Test
    void updateTeam_withLeaderAndProject_success() {
        Team existing = new Team();
        existing.setTeamId(1L);
        existing.setName("old");

        TeamDTO dto = new TeamDTO();
        dto.setName("new");
        dto.setDescription("desc");
        dto.setLeaderId(2L);
        dto.setProjectId(3L);

        User leader = new User();
        Project project = new Project();

        Team updated = new Team();
        updated.setTeamId(1L);

        when(teamRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.findById(2L)).thenReturn(Optional.of(leader));
        when(projectRepository.findById(3L)).thenReturn(Optional.of(project));
        when(teamRepository.save(existing)).thenReturn(updated);
        when(modelMapper.map(updated, TeamDTO.class)).thenReturn(dto);

        TeamDTO result = teamService.updateTeam(1L, dto);
        assertEquals("new", result.getName());
    }

    @Test
    void deleteTeam_success() {
        when(teamRepository.existsById(1L)).thenReturn(true);
        doNothing().when(teamRepository).deleteById(1L);

        teamService.deleteTeam(1L);

        verify(teamRepository).deleteById(1L);
    }

    @Test
    void deleteTeam_notFound_throws() {
        when(teamRepository.existsById(1L)).thenReturn(false);
        Exception ex = assertThrows(TeamNotFoundException.class, () -> teamService.deleteTeam(1L));
        assertEquals("Equipo no encontrado con ID: 1", ex.getMessage());
    }

}
