package com.udea.fe.controller;

import com.udea.fe.DTO.FeedbackResponseDTO;
import com.udea.fe.service.FeedbackResponseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FeedbackResponseControllerTest {

    @Mock
    private FeedbackResponseService feedbackResponseService;

    @InjectMocks
    private FeedbackResponseController feedbackResponseController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateFeedbackResponse() {
        FeedbackResponseDTO input = new FeedbackResponseDTO();
        FeedbackResponseDTO expected = new FeedbackResponseDTO();
        when(feedbackResponseService.createFeedbackResponse(input)).thenReturn(expected);

        ResponseEntity<FeedbackResponseDTO> response = feedbackResponseController.createFeedbackResponse(input);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(expected, response.getBody());
    }

    @Test
    public void testGetFeedbackResponseById() {
        Long id = 1L;
        FeedbackResponseDTO expected = new FeedbackResponseDTO();
        when(feedbackResponseService.getFeedbackResponseById(id)).thenReturn(expected);

        ResponseEntity<FeedbackResponseDTO> response = feedbackResponseController.getFeedbackResponseById(id);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expected, response.getBody());
    }

    @Test
    public void testGetAllFeedbackResponses() {
        List<FeedbackResponseDTO> expectedList = Arrays.asList(new FeedbackResponseDTO(), new FeedbackResponseDTO());
        when(feedbackResponseService.getAllFeedbackResponses()).thenReturn(expectedList);

        ResponseEntity<List<FeedbackResponseDTO>> response = feedbackResponseController.getAllFeedbackResponses();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedList, response.getBody());
    }

    @Test
    public void testUpdate() {
        Long id = 1L;
        FeedbackResponseDTO input = new FeedbackResponseDTO();
        FeedbackResponseDTO updated = new FeedbackResponseDTO();
        when(feedbackResponseService.updateFeedbackResponse(id, input)).thenReturn(updated);

        ResponseEntity<FeedbackResponseDTO> response = feedbackResponseController.update(id, input);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(updated, response.getBody());
    }

    @Test
    public void testDelete() {
        Long id = 1L;

        ResponseEntity<Void> response = feedbackResponseController.delete(id);

        assertEquals(204, response.getStatusCodeValue());
        verify(feedbackResponseService, times(1)).deleteFeedbackResponse(id);
    }
}
