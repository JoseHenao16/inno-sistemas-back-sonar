package com.udea.fe.service;

import com.udea.fe.DTO.FeedbackDTO;
import com.udea.fe.DTO.NotificationDTO;
import com.udea.fe.entity.*;
import com.udea.fe.exception.FeedbackNotFoundException;
import com.udea.fe.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class FeedbackServiceTest {

    private FeedbackRepository feedbackRepository;
    private SubmissionRepository submissionRepository;
    private UserRepository userRepository;
    private NotificationService notificationService;
    private ModelMapper modelMapper;
    private FeedbackService feedbackService;

    @BeforeEach
    void setUp() {
        feedbackRepository = mock(FeedbackRepository.class);
        submissionRepository = mock(SubmissionRepository.class);
        userRepository = mock(UserRepository.class);
        notificationService = mock(NotificationService.class);
        modelMapper = mock(ModelMapper.class);
        feedbackService = new FeedbackService(feedbackRepository, submissionRepository, userRepository, modelMapper,
                notificationService);
    }

    @Test
    void createFeedback_success() {
        FeedbackDTO dto = new FeedbackDTO();
        dto.setSubmissionId(1L);
        dto.setCreatedById(2L);
        dto.setComment("Buen trabajo");
        Submission submission = new Submission();
        User user = new User();
        user.setUserId(2L);
        submission.setUser(user);
        Feedback feedback = new Feedback();

        when(submissionRepository.findById(1L)).thenReturn(Optional.of(submission));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(modelMapper.map(dto, Feedback.class)).thenReturn(feedback);
        when(feedbackRepository.save(any())).thenReturn(feedback);
        when(modelMapper.map(any(), eq(FeedbackDTO.class))).thenReturn(dto);

        FeedbackDTO result = feedbackService.createFeedback(dto);

        assertEquals("Buen trabajo", result.getComment());
        verify(notificationService).createNotification(any(NotificationDTO.class));
    }

    @Test
    void createFeedback_submissionNotFound_throws() {
        FeedbackDTO dto = new FeedbackDTO();
        dto.setSubmissionId(1L);

        when(submissionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(FeedbackNotFoundException.class, () -> feedbackService.createFeedback(dto));
    }

    @Test
    void updateFeedback_success() {
        FeedbackDTO dto = new FeedbackDTO();
        dto.setComment("Updated");
        dto.setRating(4);

        Feedback feedback = new Feedback();
        feedback.setComment("Old");
        feedback.setRating(2);

        when(feedbackRepository.findById(1L)).thenReturn(Optional.of(feedback));
        when(feedbackRepository.save(any())).thenReturn(feedback);
        when(modelMapper.map(any(), eq(FeedbackDTO.class))).thenReturn(dto);

        FeedbackDTO result = feedbackService.updateFeedback(1L, dto);

        assertEquals("Updated", result.getComment());
    }

    @Test
    void updateFeedback_notFound_throws() {
        FeedbackDTO dto = new FeedbackDTO();
        when(feedbackRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(FeedbackNotFoundException.class, () -> feedbackService.updateFeedback(1L, dto));
    }

    @Test
    void getFeedbackById_success() {
        Feedback feedback = new Feedback();
        FeedbackDTO dto = new FeedbackDTO();
        when(feedbackRepository.findById(1L)).thenReturn(Optional.of(feedback));
        when(modelMapper.map(feedback, FeedbackDTO.class)).thenReturn(dto);

        FeedbackDTO result = feedbackService.getFeedbackById(1L);
        assertNotNull(result);
    }

    @Test
    void getFeedbackById_notFound_throws() {
        when(feedbackRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(FeedbackNotFoundException.class, () -> feedbackService.getFeedbackById(1L));
    }

    @Test
    void deleteFeedback_success() {
        when(feedbackRepository.existsById(1L)).thenReturn(true);
        doNothing().when(feedbackRepository).deleteById(1L);
        feedbackService.deleteFeedback(1L);
        verify(feedbackRepository).deleteById(1L);
    }

    @Test
    void deleteFeedback_notFound_throws() {
        when(feedbackRepository.existsById(1L)).thenReturn(false);
        assertThrows(FeedbackNotFoundException.class, () -> feedbackService.deleteFeedback(1L));
    }

    @Test
    void getAllFeedbacks_success() {
        Feedback feedback = new Feedback();
        FeedbackDTO dto = new FeedbackDTO();

        when(feedbackRepository.findAll()).thenReturn(List.of(feedback));
        when(modelMapper.map(feedback, FeedbackDTO.class)).thenReturn(dto);

        List<FeedbackDTO> result = feedbackService.getAllFeedbacks();

        assertEquals(1, result.size());
    }

    @Test
    void getFeedbacksBySubmissionId_success() {
        Feedback feedback = new Feedback();
        Submission submission = new Submission();
        submission.setSubmissionId(1L);
        feedback.setSubmission(submission);
        FeedbackDTO dto = new FeedbackDTO();

        when(feedbackRepository.findAll()).thenReturn(List.of(feedback));
        when(modelMapper.map(feedback, FeedbackDTO.class)).thenReturn(dto);

        List<FeedbackDTO> result = feedbackService.getFeedbacksBySubmissionId(1L);
        assertEquals(1, result.size());
    }

    @Test
    void createFeedback_withParentFeedback_success() {
        // Datos de entrada
        FeedbackDTO dto = new FeedbackDTO();
        dto.setSubmissionId(1L);
        dto.setCreatedById(2L);
        dto.setComment("Comentario");
        dto.setRating(4);
        dto.setParentFeedbackId(3L);

        Submission submission = new Submission();
        User user = new User();
        user.setUserId(2L);
        submission.setUser(user); // necesario para la notificación

        Feedback parent = new Feedback();
        parent.setFeedbackId(3L);

        Feedback saved = new Feedback();
        saved.setFeedbackId(10L);

        when(submissionRepository.findById(1L)).thenReturn(Optional.of(submission));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(feedbackRepository.findById(3L)).thenReturn(Optional.of(parent));
        when(feedbackRepository.save(any())).thenReturn(saved);
        when(modelMapper.map(any(Feedback.class), eq(FeedbackDTO.class))).thenReturn(dto);

        FeedbackDTO result = feedbackService.createFeedback(dto);

        assertNotNull(result);
        verify(feedbackRepository).save(any(Feedback.class));
        verify(notificationService).createNotification(any(NotificationDTO.class));
    }
}
