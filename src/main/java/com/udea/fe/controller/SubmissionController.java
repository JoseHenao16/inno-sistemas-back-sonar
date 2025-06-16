package com.udea.fe.controller;

import com.udea.fe.DTO.SubmissionRequestDTO;
import com.udea.fe.DTO.SubmissionResponseDTO;
import com.udea.fe.service.SubmissionService;
import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {

  @Autowired
  private SubmissionService submissionService;

  @PostMapping
  public ResponseEntity<?> createSubmission(
    @RequestBody SubmissionRequestDTO request
  ) {
    System.out.println("Llamada a createSubmission con request: " + request);
    try {
      SubmissionResponseDTO response = submissionService.createSubmission(
        request
      );
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      System.err.println("Error en createSubmission: " + e.getMessage());
      return ResponseEntity
        .badRequest()
        .body("Error en createSubmission: " + e.getMessage());
    }
  }

  @GetMapping
  public ResponseEntity<?> getAllSubmissions() {
    System.out.println("Llamada a getAllSubmissions");
    try {
      List<SubmissionResponseDTO> response = submissionService.getAllSubmissions();
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      System.err.println("Error en getAllSubmissions: " + e.getMessage());
      return ResponseEntity
        .badRequest()
        .body("Error en getAllSubmissions: " + e.getMessage());
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getSubmissionById(@PathVariable Long id) {
    System.out.println("Llamada a getSubmissionById con id: " + id);
    try {
      SubmissionResponseDTO response = submissionService.getSubmissionById(id);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      System.err.println("Error en getSubmissionById: " + e.getMessage());
      return ResponseEntity
        .badRequest()
        .body("Error en getSubmissionById: " + e.getMessage());
    }
  }

  @GetMapping("/by-task/{taskId}")
  public ResponseEntity<?> getSubmissionsByTask(
    @PathVariable Long taskId,
    Principal principal
  ) {
    System.out.println("Llamada a getSubmissionsByTask con taskId: " + taskId);
    try {
      String userEmail = principal.getName();
      List<SubmissionResponseDTO> response = submissionService.getSubmissionsByTaskId(
        taskId,
        userEmail
      );
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      System.err.println("Error en getSubmissionsByTask: " + e.getMessage());
      return ResponseEntity
        .badRequest()
        .body("Error en getSubmissionsByTask: " + e.getMessage());
    }
  }
}
