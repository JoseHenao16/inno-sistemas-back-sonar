package com.udea.fe.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.udea.fe.DTO.TaskAssignmentRequestDTO;
import com.udea.fe.DTO.TaskAssignmentResponseDTO;
import com.udea.fe.entity.User;
import com.udea.fe.service.TaskAssignmentService;

@RestController
@RequestMapping("/api/task-assignments")
public class TaskAssignmentController {

    @Autowired
    private TaskAssignmentService taskAssignmentService;

    @PostMapping
    public ResponseEntity<?> assignTask(@RequestBody TaskAssignmentRequestDTO request) {
        try {
            System.out.println("Asignando tarea al usuario con ID: " + request.getAssignedId() + " y tarea ID: " + request.getTaskId());
            TaskAssignmentResponseDTO response = taskAssignmentService.assignTask(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error al asignar la tarea: " + e.getMessage());
            return ResponseEntity.badRequest().body("Error al asignar la tarea: " + e.getMessage());
        }
    }

    @GetMapping("/task/{taskId}/users")
    public ResponseEntity<?> getUsersAssignedToTask(@PathVariable Long taskId) {
        try {
            System.out.println("Obteniendo usuarios asignados a la tarea con ID: " + taskId);
            List<User> users = taskAssignmentService.getUsersAssignedToTask(taskId);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            System.err.println("Error al obtener usuarios asignados: " + e.getMessage());
            return ResponseEntity.badRequest().body("Error al obtener usuarios asignados: " + e.getMessage());
        }
    }
}
