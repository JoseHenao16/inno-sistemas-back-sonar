package com.udea.fe.controller;

import com.udea.fe.DTO.TeamDTO;
import com.udea.fe.entity.UserTeam;
import com.udea.fe.service.TeamService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TeamControllerTest {

    @Mock
    private TeamService teamService;

    @InjectMocks
    private TeamController teamController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTeam() {
        TeamDTO teamDTO = new TeamDTO();
        when(teamService.createTeam(teamDTO)).thenReturn(teamDTO);

        ResponseEntity<TeamDTO> response = teamController.createTeam(teamDTO);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(teamDTO, response.getBody());
    }

    @Test
    void testGetTeamById() {
        Long id = 1L;
        TeamDTO teamDTO = new TeamDTO();
        when(teamService.getTeamById(id)).thenReturn(teamDTO);

        ResponseEntity<TeamDTO> response = teamController.getTeamById(id);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(teamDTO, response.getBody());
    }

    @Test
    void testGetTeamsByProject() {
        Long projectId = 1L;
        List<TeamDTO> teams = Arrays.asList(new TeamDTO(), new TeamDTO());
        when(teamService.getTeamsByProject(projectId)).thenReturn(teams);

        ResponseEntity<List<TeamDTO>> response = teamController.getTeamsByProject(projectId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(teams, response.getBody());
    }

    @Test
    void testUpdateTeam() {
        Long id = 1L;
        TeamDTO dto = new TeamDTO();
        when(teamService.updateTeam(id, dto)).thenReturn(dto);

        ResponseEntity<TeamDTO> response = teamController.updateTeam(id, dto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(dto, response.getBody());
    }

    @Test
    void testDeleteTeam() {
        Long id = 1L;
        doNothing().when(teamService).deleteTeam(id);

        ResponseEntity<String> response = teamController.deleteTeam(id);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Equipo eliminado correctamente.", response.getBody());
    }

    @Test
    void testAddUserToTeam() {
        Long teamId = 1L;
        Long userId = 2L;
        String role = "Líder";

        doNothing().when(teamService).addUserToTeam(userId, teamId, role);

        ResponseEntity<String> response = teamController.addUserToTeam(teamId, userId, role);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Usuario agregado al equipo correctamente.", response.getBody());
    }

    @Test
    void testGetUsersByTeam() {
        Long teamId = 1L;
        List<UserTeam> users = Arrays.asList(new UserTeam(), new UserTeam());
        when(teamService.getUsersByTeam(teamId)).thenReturn(users);

        ResponseEntity<List<UserTeam>> response = teamController.getUsersByTeam(teamId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(users, response.getBody());
    }

    @Test
    void testRemoveUserFromTeam() {
        Long teamId = 1L;
        Long userId = 2L;

        doNothing().when(teamService).removeUserFromTeam(userId, teamId);

        ResponseEntity<String> response = teamController.removeUserFromTeam(teamId, userId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Usuario eliminado del equipo correctamente.", response.getBody());
    }
}
