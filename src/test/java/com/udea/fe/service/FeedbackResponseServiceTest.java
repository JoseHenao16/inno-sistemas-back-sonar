package com.udea.fe.service;

import com.udea.fe.DTO.FeedbackResponseDTO;
import com.udea.fe.entity.Feedback;
import com.udea.fe.entity.FeedbackResponse;
import com.udea.fe.entity.User;
import com.udea.fe.exception.FeedbackResponseNotFoundException;
import com.udea.fe.repository.FeedbackRepository;
import com.udea.fe.repository.FeedbackResponseRepository;
import com.udea.fe.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class FeedbackResponseServiceTest {

    private FeedbackResponseRepository feedbackResponseRepository;
    private FeedbackRepository feedbackRepository;
    private UserRepository userRepository;
    private ModelMapper modelMapper;
    private FeedbackResponseService service;

    @BeforeEach
    void setUp() {
        feedbackResponseRepository = mock(FeedbackResponseRepository.class);
        feedbackRepository = mock(FeedbackRepository.class);
        userRepository = mock(UserRepository.class);
        modelMapper = mock(ModelMapper.class);
        service = new FeedbackResponseService(
                feedbackResponseRepository,
                feedbackRepository,
                userRepository,
                modelMapper);
    }

    @Test
    void createFeedbackResponse_success() {
        FeedbackResponseDTO dto = new FeedbackResponseDTO();
        dto.setFeedbackId(1L);
        dto.setCreatedById(2L);

        Feedback feedback = new Feedback();
        User user = new User();
        FeedbackResponse response = new FeedbackResponse();
        FeedbackResponse saved = new FeedbackResponse();

        when(feedbackRepository.findById(1L)).thenReturn(Optional.of(feedback));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(modelMapper.map(dto, FeedbackResponse.class)).thenReturn(response);
        when(feedbackResponseRepository.save(response)).thenReturn(saved);
        when(modelMapper.map(saved, FeedbackResponseDTO.class)).thenReturn(dto);

        FeedbackResponseDTO result = service.createFeedbackResponse(dto);

        assertEquals(dto, result);
    }

    @Test
    void createFeedbackResponse_feedbackNotFound_throwsException() {
        FeedbackResponseDTO dto = new FeedbackResponseDTO();
        dto.setFeedbackId(1L);

        when(feedbackRepository.findById(1L)).thenReturn(Optional.empty());

        FeedbackResponseNotFoundException ex = assertThrows(
                FeedbackResponseNotFoundException.class,
                () -> service.createFeedbackResponse(dto));

        assertEquals("Feedback no encontrado con id: 1", ex.getMessage());
    }

    @Test
    void createFeedbackResponse_userNotFound_throwsException() {
        FeedbackResponseDTO dto = new FeedbackResponseDTO();
        dto.setFeedbackId(1L);
        dto.setCreatedById(99L);
        dto.setComment("Respuesta");

        Feedback feedback = new Feedback();

        when(modelMapper.map(any(FeedbackResponseDTO.class), eq(FeedbackResponse.class)))
                .thenReturn(new FeedbackResponse());
        when(feedbackRepository.findById(1L)).thenReturn(Optional.of(feedback));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        FeedbackResponseNotFoundException ex = assertThrows(
                FeedbackResponseNotFoundException.class,
                () -> service.createFeedbackResponse(dto) // <- usa `service`, no `feedbackResponseService`
        );
        assertEquals("Usuario no encontrado con id: 99", ex.getMessage());
    }

    @Test
    void getFeedbackResponseById_success() {
        FeedbackResponse response = new FeedbackResponse();
        FeedbackResponseDTO dto = new FeedbackResponseDTO();

        when(feedbackResponseRepository.findById(1L)).thenReturn(Optional.of(response));
        when(modelMapper.map(response, FeedbackResponseDTO.class)).thenReturn(dto);

        FeedbackResponseDTO result = service.getFeedbackResponseById(1L);

        assertEquals(dto, result);
    }

    @Test
    void getFeedbackResponseById_notFound_throwsException() {
        when(feedbackResponseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                FeedbackResponseNotFoundException.class,
                () -> service.getFeedbackResponseById(1L));
    }

    @Test
    void getAllFeedbackResponses_success() {
        FeedbackResponse response = new FeedbackResponse();
        FeedbackResponseDTO dto = new FeedbackResponseDTO();

        when(feedbackResponseRepository.findAll()).thenReturn(List.of(response));
        when(modelMapper.map(response, FeedbackResponseDTO.class)).thenReturn(dto);

        List<FeedbackResponseDTO> result = service.getAllFeedbackResponses();

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    @Test
    void updateFeedbackResponse_success() {
        FeedbackResponse existing = new FeedbackResponse();
        FeedbackResponseDTO dto = new FeedbackResponseDTO();
        dto.setComment("Updated");

        when(feedbackResponseRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(feedbackResponseRepository.save(existing)).thenReturn(existing);
        when(modelMapper.map(existing, FeedbackResponseDTO.class)).thenReturn(dto);

        FeedbackResponseDTO result = service.updateFeedbackResponse(1L, dto);

        assertEquals("Updated", result.getComment());
    }

    @Test
    void updateFeedbackResponse_notFound_throwsException() {
        FeedbackResponseDTO dto = new FeedbackResponseDTO();

        when(feedbackResponseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                FeedbackResponseNotFoundException.class,
                () -> service.updateFeedbackResponse(1L, dto));
    }

    @Test
    void deleteFeedbackResponse_success() {
        when(feedbackResponseRepository.existsById(1L)).thenReturn(true);
        service.deleteFeedbackResponse(1L);
        verify(feedbackResponseRepository).deleteById(1L);
    }

    @Test
    void deleteFeedbackResponse_notFound_throwsException() {
        when(feedbackResponseRepository.existsById(1L)).thenReturn(false);

        assertThrows(
                FeedbackResponseNotFoundException.class,
                () -> service.deleteFeedbackResponse(1L));
    }
}
