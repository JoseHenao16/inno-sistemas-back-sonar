package com.udea.fe.controller;

import com.udea.fe.DTO.SubmissionRequestDTO;
import com.udea.fe.DTO.SubmissionResponseDTO;
import com.udea.fe.service.SubmissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SubmissionControllerTest {

    @Mock
    private SubmissionService submissionService;

    @Mock
    private Principal principal;

    @InjectMocks
    private SubmissionController submissionController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateSubmissionSuccess() {
        SubmissionRequestDTO request = new SubmissionRequestDTO();
        SubmissionResponseDTO response = new SubmissionResponseDTO();

        when(submissionService.createSubmission(request)).thenReturn(response);

        ResponseEntity<SubmissionResponseDTO> result = submissionController.createSubmission(request);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(response, result.getBody());
    }

    @Test
    void testCreateSubmissionFailure() {
        SubmissionRequestDTO request = new SubmissionRequestDTO();

        when(submissionService.createSubmission(request)).thenThrow(new RuntimeException("Error"));

        ResponseEntity<SubmissionResponseDTO> result = submissionController.createSubmission(request);

        assertEquals(400, result.getStatusCodeValue());
    }

    @Test
    void testGetAllSubmissionsSuccess() {
        List<SubmissionResponseDTO> mockList = Arrays.asList(new SubmissionResponseDTO(), new SubmissionResponseDTO());

        when(submissionService.getAllSubmissions()).thenReturn(mockList);

        ResponseEntity<List<SubmissionResponseDTO>> result = submissionController.getAllSubmissions();

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(mockList, result.getBody());
    }

    @Test
    void testGetAllSubmissionsFailure() {
        when(submissionService.getAllSubmissions()).thenThrow(new RuntimeException("Error"));

        ResponseEntity<List<SubmissionResponseDTO>> result = submissionController.getAllSubmissions();

        assertEquals(400, result.getStatusCodeValue());
    }

    @Test
    void testGetSubmissionByIdSuccess() {
        Long id = 1L;
        SubmissionResponseDTO response = new SubmissionResponseDTO();

        when(submissionService.getSubmissionById(id)).thenReturn(response);

        ResponseEntity<SubmissionResponseDTO> result = submissionController.getSubmissionById(id);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(response, result.getBody());
    }

    @Test
    void testGetSubmissionByIdFailure() {
        Long id = 1L;

        when(submissionService.getSubmissionById(id)).thenThrow(new RuntimeException("Error"));

        ResponseEntity<SubmissionResponseDTO> result = submissionController.getSubmissionById(id);

        assertEquals(400, result.getStatusCodeValue());
    }

    @Test
    void testGetSubmissionsByTaskSuccess() {
        Long taskId = 1L;
        String email = "user@example.com";
        List<SubmissionResponseDTO> mockList = Arrays.asList(new SubmissionResponseDTO());

        when(principal.getName()).thenReturn(email);
        when(submissionService.getSubmissionsByTaskId(taskId, email)).thenReturn(mockList);

        ResponseEntity<List<SubmissionResponseDTO>> result = submissionController.getSubmissionsByTask(taskId, principal);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(mockList, result.getBody());
    }

    @Test
    void testGetSubmissionsByTaskFailure() {
        Long taskId = 1L;
        String email = "user@example.com";

        when(principal.getName()).thenReturn(email);
        when(submissionService.getSubmissionsByTaskId(taskId, email)).thenThrow(new RuntimeException("Error"));

        ResponseEntity<List<SubmissionResponseDTO>> result = submissionController.getSubmissionsByTask(taskId, principal);

        assertEquals(400, result.getStatusCodeValue());
    }
}
