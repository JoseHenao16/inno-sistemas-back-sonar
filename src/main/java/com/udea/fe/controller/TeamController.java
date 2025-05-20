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
        System.out.println("\n ---> Creando un nuevo equipo: " + teamDTO.getName());
        return new ResponseEntity<>(teamService.createTeam(teamDTO), HttpStatus.CREATED);
    }

    // Obtener equipo por ID
    @GetMapping("/{id}")
    public ResponseEntity<TeamDTO> getTeamById(@PathVariable Long id) {
        System.out.println("\n ---> Obteniendo información del equipo con ID: " + id);
        return ResponseEntity.ok(teamService.getTeamById(id));
    }

    // Obtener equipos por ID de proyecto
    @GetMapping("/project/{projectId}/all")
    public ResponseEntity<List<TeamDTO>> getTeamsByProject(@PathVariable Long projectId) {
        System.out.println("\n ---> Listando todos los equipos del proyecto con ID: " + projectId);
        return ResponseEntity.ok(teamService.getTeamsByProject(projectId));
    }

    // Actualizar un equipo
    @PutMapping("/{id}/edit")
    public ResponseEntity<TeamDTO> updateTeam(
            @PathVariable Long id,
            @RequestBody TeamDTO teamDTO
    ) {
        System.out.println("\n ---> Actualizando equipo con ID: " + id + " | Nuevos datos: " + teamDTO.getName());
        return ResponseEntity.ok(teamService.updateTeam(id, teamDTO));
    }

    // Eliminar un equipo
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<String> deleteTeam(@PathVariable Long id) {
        System.out.println("\n ---> Eliminando equipo con ID: " + id);
        teamService.deleteTeam(id);
        return ResponseEntity.ok("Equipo eliminado correctamente.");
    }

    // Agregar usuario a un equipo
    @PostMapping("/{teamId}/users/{userId}")
    public ResponseEntity<String> addUserToTeam(
            @PathVariable Long teamId,
            @PathVariable Long userId,
            @RequestParam(defaultValue = "Miembro") String roleInGroup
    ) {
        System.out.println("\n ---> Agregando usuario con ID: " + userId + " al equipo con ID: " + teamId + " como: " + roleInGroup);
        teamService.addUserToTeam(userId, teamId, roleInGroup);
        return ResponseEntity.ok("Usuario agregado al equipo correctamente.");
    }

    // Listar usuarios de un equipo
    @GetMapping("/{teamId}/users")
    public ResponseEntity<List<UserTeam>> getUsersByTeam(@PathVariable Long teamId) {
        System.out.println("\n ---> Listando usuarios del equipo con ID: " + teamId);
        return ResponseEntity.ok(teamService.getUsersByTeam(teamId));
    }

    // Remover usuario de un equipo
    @DeleteMapping("/{teamId}/users/{userId}")
    public ResponseEntity<String> removeUserFromTeam(
            @PathVariable Long teamId,
            @PathVariable Long userId
    ) {
        System.out.println("\n ---> Eliminando usuario con ID: " + userId + " del equipo con ID: " + teamId);
        teamService.removeUserFromTeam(userId, teamId);
        return ResponseEntity.ok("Usuario eliminado del equipo correctamente.");
    }
}
