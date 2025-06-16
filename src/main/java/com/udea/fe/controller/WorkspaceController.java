package com.udea.fe.controller;

import com.udea.fe.DTO.WorkspaceDTO;
import com.udea.fe.service.WorkspaceService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workspaces")
@AllArgsConstructor
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    @PostMapping
    public ResponseEntity<WorkspaceDTO> createWorkspace(@RequestBody WorkspaceDTO workspaceDTO) {
        WorkspaceDTO createdWorkspace = workspaceService.createWorkspace(workspaceDTO);
        return new ResponseEntity<>(createdWorkspace, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkspaceDTO> getWorkspaceById(@PathVariable Long id) {
        WorkspaceDTO workspaceDTO = workspaceService.getWorkspaceById(id);
        return ResponseEntity.ok(workspaceDTO);
    }

    @GetMapping("/all")
    public ResponseEntity<List<WorkspaceDTO>> getAllWorkspaces() {
        List<WorkspaceDTO> workspaces = workspaceService.getAllWorkspaces();
        return ResponseEntity.ok(workspaces);
    }

    @PutMapping("/{id}/edit")
    public ResponseEntity<WorkspaceDTO> updateWorkspace(@PathVariable Long id, @RequestBody WorkspaceDTO workspaceDTO) {
        WorkspaceDTO updatedWorkspace = workspaceService.updateWorkspace(id, workspaceDTO);
        return ResponseEntity.ok(updatedWorkspace);
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> deleteWorkspace(@PathVariable Long id) {
        workspaceService.deleteWorkspace(id);
        return ResponseEntity.noContent().build();
    }
}
