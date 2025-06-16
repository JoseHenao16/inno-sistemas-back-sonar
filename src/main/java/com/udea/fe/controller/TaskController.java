package com.udea.fe.controller;

import com.udea.fe.DTO.TaskDTO;
import com.udea.fe.entity.TaskStatus;
import com.udea.fe.service.TaskService;
import java.security.Principal;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@AllArgsConstructor
public class TaskController {

  private final TaskService taskService;

  @PostMapping("/create_task")
  public ResponseEntity<TaskDTO> createTask(@RequestBody TaskDTO taskDTO) {
    System.out.println("\n ---> Creando nueva tarea: " + taskDTO);
    TaskDTO createdTask = taskService.createTask(taskDTO);
    return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  public ResponseEntity<TaskDTO> getTaskById(@PathVariable Long id) {
    System.out.println("\n ---> Obteniendo tarea con ID: " + id);
    TaskDTO taskDTO = taskService.getTaskById(id);
    return ResponseEntity.ok(taskDTO);
  }

  @GetMapping("/all")
  public ResponseEntity<List<TaskDTO>> getAllTasks() {
    System.out.println("\n ---> Obteniendo todas las tareas");
    List<TaskDTO> tasks = taskService.getAllTasks();
    return ResponseEntity.ok(tasks);
  }

  @PutMapping("/{id}/edit")
  public ResponseEntity<TaskDTO> updateTask(
    @PathVariable Long id,
    @RequestBody TaskDTO taskDTO
  ) {
    System.out.println(
      "\n ---> Actualizando tarea con ID: " + id + " con datos: " + taskDTO
    );
    TaskDTO updatedTask = taskService.updateTask(id, taskDTO);
    return ResponseEntity.ok(updatedTask);
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<TaskDTO> updateTaskStatus(
    @PathVariable Long id,
    @RequestParam TaskStatus status
  ) {
    System.out.println(
      "\n ---> Actualizando estado de la tarea con ID: " + id + " a: " + status
    );
    TaskDTO updatedTask = taskService.updateTaskStatus(id, status);
    return ResponseEntity.ok(updatedTask);
  }

  @GetMapping("/project/{projectId}")
  public ResponseEntity<List<TaskDTO>> getTasksByProject(
    @PathVariable Long projectId,
    Principal principal
  ) {
    System.out.println(
      "\n ---> Obteniendo tareas del proyecto con ID: " + projectId
    );
    String userEmail = principal.getName();
    List<TaskDTO> tasks = taskService.getTasksByProjectIdAndUser(
      projectId,
      userEmail
    );
    return ResponseEntity.ok(tasks);
  }
}
