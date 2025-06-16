package com.udea.fe.controller;

import com.udea.fe.DTO.FeedbackResponseDTO;
import com.udea.fe.service.FeedbackResponseService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback-response")
@AllArgsConstructor
public class FeedbackResponseController {

    private final FeedbackResponseService feedbackResponseService;


    @PostMapping("/create_feedbackResponse")
    public ResponseEntity<FeedbackResponseDTO> createFeedbackResponse(@RequestBody FeedbackResponseDTO feedbackResponseDTO) {
        FeedbackResponseDTO createdFeedbackResponse = feedbackResponseService.createFeedbackResponse(feedbackResponseDTO);
        return new ResponseEntity<>(createdFeedbackResponse, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeedbackResponseDTO> getFeedbackResponseById(@PathVariable Long id) {
        FeedbackResponseDTO feedbackResponse = feedbackResponseService.getFeedbackResponseById(id);
        return ResponseEntity.ok(feedbackResponse);
    }

    @GetMapping("/all")
    public ResponseEntity<List<FeedbackResponseDTO>> getAllFeedbackResponses() {
        List<FeedbackResponseDTO> feedbackResponses = feedbackResponseService.getAllFeedbackResponses();
        return ResponseEntity.ok(feedbackResponses);
    }

    @PutMapping("/{id}/edit")
    public ResponseEntity<FeedbackResponseDTO> update(@PathVariable Long id, @RequestBody FeedbackResponseDTO dto) {
        FeedbackResponseDTO updated = feedbackResponseService.updateFeedbackResponse(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        feedbackResponseService.deleteFeedbackResponse(id);
        return ResponseEntity.noContent().build();
    }
}
