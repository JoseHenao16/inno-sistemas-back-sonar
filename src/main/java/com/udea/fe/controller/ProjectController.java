package com.udea.fe.controller;

import com.udea.fe.DTO.ProjectDTO;
import com.udea.fe.entity.ProjectStatus;
import com.udea.fe.service.ProjectService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/project")
@AllArgsConstructor
public class ProjectController {

  private final ProjectService projectService;

  @GetMapping("/my-projects/{userId}")
  public ResponseEntity<List<ProjectDTO>> getMyProjects(
    @PathVariable Long userId
  ) {
    try {
      System.out.println(
        "\n ---> Obteniendo proyectos del usuario con ID: " + userId
      );
      List<ProjectDTO> projects = projectService.getProjectsByUserId(userId);
      System.out.println("Proyectos encontrados: " + projects.size());
      return ResponseEntity.ok(projects);
    } catch (Exception e) {
      System.out.println(
        "Error al obtener proyectos del usuario con ID: " + userId
      );
      e.printStackTrace();
      return ResponseEntity.status(500).body(null);
    }
  }

  @PostMapping("/create_project")
  public ResponseEntity<ProjectDTO> createProject(
    @Valid @RequestBody ProjectDTO projectDTO
  ) {
    ProjectDTO createdProject = projectService.createProject(projectDTO);
    return new ResponseEntity<>(createdProject, HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ProjectDTO> getProjectById(@PathVariable Long id) {
    ProjectDTO projectDTO = projectService.getProjectById(id);
    return ResponseEntity.ok(projectDTO);
  }

  @GetMapping("/all")
  public ResponseEntity<List<ProjectDTO>> getAllProjects() {
    List<ProjectDTO> projects = projectService.getAllProjects();
    return ResponseEntity.ok(projects);
  }

  @PutMapping("/{id}/edit")
  public ResponseEntity<ProjectDTO> updateProject(
    @PathVariable Long id,
    @Valid @RequestBody ProjectDTO projectDTO
  ) {
    ProjectDTO updatedProject = projectService.updateProject(id, projectDTO);
    return ResponseEntity.ok(updatedProject);
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<ProjectDTO> updateProjectStatus(
    @PathVariable Long id,
    @RequestParam ProjectStatus status
  ) {
    ProjectDTO updatedProject = projectService.changeProjectStatus(id, status);
    return ResponseEntity.ok(updatedProject);
  }
}
