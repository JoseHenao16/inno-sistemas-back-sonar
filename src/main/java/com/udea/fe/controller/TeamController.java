package com.udea.fe.controller;


import com.udea.fe.DTO.TeamDTO;
import com.udea.fe.entity.UserTeam;
import com.udea.fe.service.TeamService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@AllArgsConstructor
public class TeamController {
    private final TeamService teamService;

    // Crear equipo
    @PostMapping("create_team")
    public ResponseEntity<TeamDTO> createTeam(@RequestBody TeamDTO teamDTO) {
        return new ResponseEntity<>(teamService.createTeam(teamDTO), HttpStatus.CREATED);
    }

    // Obtener equipo por ID
    @GetMapping("/{id}")
    public ResponseEntity<TeamDTO> getTeamById(@PathVariable Long id) {
        return ResponseEntity.ok(teamService.getTeamById(id));
    }

    // Obtener equipos por ID de proyecto
    @GetMapping("/project/{projectId}/all")
    public ResponseEntity<List<TeamDTO>> getTeamsByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(teamService.getTeamsByProject(projectId));
    }

    // Actualizar un equipo
    @PutMapping("/{id}/edit")
    public ResponseEntity<TeamDTO> updateTeam(
            @PathVariable Long id,
            @RequestBody TeamDTO teamDTO
    ) {
        return ResponseEntity.ok(teamService.updateTeam(id, teamDTO));
    }


    @DeleteMapping("/{id}/delete")
    public ResponseEntity<String> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return ResponseEntity.ok("Equipo eliminado correctamente.");
    }

    //endpoint para usuarios de equipo

    // agregar usuario a un equipo
    @PostMapping("/{teamId}/users/{userId}")
    public ResponseEntity<String> addUserToTeam(
            @PathVariable Long teamId,
            @PathVariable Long userId,
            @RequestParam(defaultValue = "Miembro") String roleInGroup
    ) {
        teamService.addUserToTeam(userId, teamId, roleInGroup);
        return ResponseEntity.ok("Usuario agregado al equipo correctamente.");
    }

    // listar usuarios de un equipo
    @GetMapping("/{teamId}/users")
    public ResponseEntity<List<UserTeam>> getUsersByTeam(@PathVariable Long teamId) {
        return ResponseEntity.ok(teamService.getUsersByTeam(teamId));
    }

    // remover usuario de un equipo
    @DeleteMapping("/{teamId}/users/{userId}")
    public ResponseEntity<String> removeUserFromTeam(
            @PathVariable Long teamId,
            @PathVariable Long userId
    ) {
        teamService.removeUserFromTeam(userId, teamId);
        return ResponseEntity.ok("Usuario eliminado del equipo correctamente.");
    }
}
