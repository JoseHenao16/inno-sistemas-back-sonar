package com.udea.fe.service;

import com.udea.fe.DTO.FeedbackDTO;
import com.udea.fe.DTO.NotificationDTO;
import com.udea.fe.entity.Feedback;
import com.udea.fe.entity.Submission;
import com.udea.fe.entity.User;
import com.udea.fe.repository.FeedbackRepository;
import com.udea.fe.repository.SubmissionRepository;
import com.udea.fe.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@Transactional
@AllArgsConstructor
public class FeedbackService {

  private final FeedbackRepository feedbackRepository;
  private final SubmissionRepository submissionRepository;
  private final UserRepository userRepository;
  private final ModelMapper modelMapper;
  private final NotificationService notificationService;

  public FeedbackDTO createFeedback(FeedbackDTO feedbackDTO) {
    Feedback feedback = modelMapper.map(feedbackDTO, Feedback.class);

    Submission submission = submissionRepository
      .findById(feedbackDTO.getSubmissionId())
      .orElseThrow(() ->
        new RuntimeException(
          "Entrega no encontrada con id: " + feedbackDTO.getSubmissionId() + ""
        )
      );
    feedback.setSubmission(submission);

    User createdBy = userRepository
      .findById(feedbackDTO.getCreatedById())
      .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    feedback.setCreatedBy(createdBy);

    if (feedbackDTO.getParentFeedbackId() != null) {
      Feedback parentFeedback = feedbackRepository
        .findById(feedbackDTO.getParentFeedbackId())
        .orElseThrow(() ->
          new RuntimeException(
            "Retroalimentacion padre no encontrada con id: " +
            feedbackDTO.getParentFeedbackId() +
            ""
          )
        );
      feedback.setParentFeedback(parentFeedback);
    }

    feedback.setCreatedAt(LocalDateTime.now());

    Feedback savedFeedback = feedbackRepository.save(feedback);

    NotificationDTO notification = new NotificationDTO();
    notification.setUserId(submission.getUser().getUserId());
    notification.setMessage("Has recibido una nueva retroalimentación.");
    notification.setType("FEEDBACK");

    notificationService.createNotification(notification);

    return modelMapper.map(savedFeedback, FeedbackDTO.class);
  }

  public FeedbackDTO updateFeedback(Long id, FeedbackDTO feedbackDTO) {
    return feedbackRepository
      .findById(id)
      .map(feedback -> {
        feedback.setComment(feedbackDTO.getComment());
        feedback.setRating(feedbackDTO.getRating());
        // No se modifica: task, createdBy, parentFeedback
        Feedback updated = feedbackRepository.save(feedback);
        return modelMapper.map(updated, FeedbackDTO.class);
      })
      .orElseThrow(() ->
        new RuntimeException("Retroalimentación no encontrada con id: " + id)
      );
  }

  public FeedbackDTO getFeedbackById(Long id) {
    return feedbackRepository
      .findById(id)
      .map(feedback -> modelMapper.map(feedback, FeedbackDTO.class))
      .orElseThrow(() ->
        new RuntimeException("Retroalimentación no encontrada con id: " + id)
      );
  }

  public List<FeedbackDTO> getAllFeedbacks() {
    return feedbackRepository
      .findAll()
      .stream()
      .map(feedback -> modelMapper.map(feedback, FeedbackDTO.class))
      .collect(Collectors.toList());
  }

  public void deleteFeedback(Long id) {
    if (!feedbackRepository.existsById(id)) {
      throw new RuntimeException(
        "Retroalimentación no encontrada con id: " + id
      );
    }
    feedbackRepository.deleteById(id);
  }

  public List<FeedbackDTO> getFeedbacksBySubmissionId(Long submissionId) {
    return feedbackRepository
      .findAll()
      .stream()
      .filter(feedback ->
        feedback.getSubmission() != null &&
        feedback.getSubmission().getSubmissionId().equals(submissionId)
      )
      .map(feedback -> modelMapper.map(feedback, FeedbackDTO.class))
      .collect(Collectors.toList());
  }
}
