
package com.udea.fe.controller;

import com.udea.fe.DTO.FeedbackDTO;
import com.udea.fe.service.FeedbackService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class FeedbackControllerTest {

    @Mock
    private FeedbackService feedbackService;

    @InjectMocks
    private FeedbackController feedbackController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateFeedback() {
        FeedbackDTO dto = new FeedbackDTO();
        when(feedbackService.createFeedback(dto)).thenReturn(dto);

        ResponseEntity<FeedbackDTO> response = feedbackController.createFeedback(dto);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(dto, response.getBody());
    }

    @Test
    void testUpdateFeedback() {
        FeedbackDTO dto = new FeedbackDTO();
        when(feedbackService.updateFeedback(1L, dto)).thenReturn(dto);

        ResponseEntity<FeedbackDTO> response = feedbackController.updateFeedback(1L, dto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(dto, response.getBody());
    }

    @Test
    void testGetFeedbackById() {
        FeedbackDTO dto = new FeedbackDTO();
        when(feedbackService.getFeedbackById(1L)).thenReturn(dto);

        ResponseEntity<FeedbackDTO> response = feedbackController.getFeedbackById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(dto, response.getBody());
    }

    @Test
    void testGetAllFeedbacks() {
        List<FeedbackDTO> feedbacks = Arrays.asList(new FeedbackDTO(), new FeedbackDTO());
        when(feedbackService.getAllFeedbacks()).thenReturn(feedbacks);

        ResponseEntity<List<FeedbackDTO>> response = feedbackController.getAllFeedbacks();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(feedbacks, response.getBody());
    }

    @Test
    void testDeleteFeedback() {
        doNothing().when(feedbackService).deleteFeedback(1L);

        ResponseEntity<Void> response = feedbackController.deleteFeedback(1L);

        assertEquals(204, response.getStatusCodeValue());
        verify(feedbackService).deleteFeedback(1L);
    }

    @Test
    void testGetFeedbacksBySubmissionSuccess() {
        List<FeedbackDTO> feedbacks = Arrays.asList(new FeedbackDTO(), new FeedbackDTO());
        when(feedbackService.getFeedbacksBySubmissionId(10L)).thenReturn(feedbacks);

        ResponseEntity<List<FeedbackDTO>> response = feedbackController.getFeedbacksBySubmission(10L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(feedbacks, response.getBody());
    }

    @Test
    void testGetFeedbacksBySubmissionFailure() {
        when(feedbackService.getFeedbacksBySubmissionId(10L)).thenThrow(new RuntimeException("Error"));

        ResponseEntity<List<FeedbackDTO>> response = feedbackController.getFeedbacksBySubmission(10L);

        assertEquals(400, response.getStatusCodeValue());
    }
}
