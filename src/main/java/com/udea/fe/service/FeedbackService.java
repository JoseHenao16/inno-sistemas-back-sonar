package com.udea.fe.service;

import com.udea.fe.DTO.FeedbackDTO;
import com.udea.fe.entity.Feedback;
import com.udea.fe.entity.Task;
import com.udea.fe.entity.User;
import com.udea.fe.repository.FeedbackRepository;
import com.udea.fe.repository.TaskRepository;
import com.udea.fe.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public FeedbackDTO createFeedback(FeedbackDTO feedbackDTO) {
        Feedback feedback = modelMapper.map(feedbackDTO, Feedback.class);

        Task task = taskRepository.findById(feedbackDTO.getTaskId())
                .orElseThrow(() -> new RuntimeException("tarea no encontrada con id: " + feedbackDTO.getTaskId() + ""));
        feedback.setTask(task);

        User createdBy = userRepository.findById(feedbackDTO.getCreatedById())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        feedback.setCreatedBy(createdBy);

        if (feedbackDTO.getParentFeedbackId() != null) {
            Feedback parentFeedback = feedbackRepository.findById(feedbackDTO.getParentFeedbackId())
                    .orElseThrow(() -> new RuntimeException
                            ("Retroalimentacion padre no encontrada con id: " + feedbackDTO.getParentFeedbackId() + ""));
            feedback.setParentFeedback(parentFeedback);
        }

        feedback.setCreatedAt(LocalDateTime.now());

        Feedback savedFeedback = feedbackRepository.save(feedback);
        return modelMapper.map(savedFeedback, FeedbackDTO.class);
    }

    public FeedbackDTO updateFeedback(Long id, FeedbackDTO feedbackDTO) {
        return feedbackRepository.findById(id)
                .map(feedback -> {
                    feedback.setComment(feedbackDTO.getComment());
                    feedback.setRating(feedbackDTO.getRating());
                    // No se modifica: task, createdBy, parentFeedback
                    Feedback updated = feedbackRepository.save(feedback);
                    return modelMapper.map(updated, FeedbackDTO.class);
                })
                .orElseThrow(() -> new RuntimeException("Retroalimentación no encontrada con id: " + id));
    }

    public FeedbackDTO getFeedbackById(Long id) {
        return feedbackRepository.findById(id)
                .map(feedback -> modelMapper.map(feedback, FeedbackDTO.class))
                .orElseThrow(() -> new RuntimeException("Retroalimentación no encontrada con id: " + id));
    }

    public List<FeedbackDTO> getAllFeedbacks() {
        return feedbackRepository.findAll()
                .stream()
                .map(feedback -> modelMapper.map(feedback, FeedbackDTO.class))
                .collect(Collectors.toList());
    }


    public void deleteFeedback(Long id) {
        if (!feedbackRepository.existsById(id)) {
            throw new RuntimeException("Retroalimentación no encontrada con id: " + id);
        }
        feedbackRepository.deleteById(id);
    }


}

