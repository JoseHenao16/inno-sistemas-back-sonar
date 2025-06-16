package com.udea.fe.service;

import com.udea.fe.DTO.NotificationDTO;
import com.udea.fe.entity.Notification;
import com.udea.fe.entity.User;
import com.udea.fe.repository.NotificationRepository;
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
public class NotificationService {

  private final NotificationRepository notificationRepository;
  private final UserRepository userRepository;
  private final ModelMapper modelMapper;

  public NotificationDTO createNotification(NotificationDTO dto) {
    Notification notification = modelMapper.map(dto, Notification.class);

    User user = userRepository
      .findById(dto.getUserId())
      .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    notification.setUser(user);
    notification.setRead(false);
    notification.setCreatedAt(LocalDateTime.now());

    Notification saved = notificationRepository.save(notification);
    return modelMapper.map(saved, NotificationDTO.class);
  }

  public NotificationDTO getById(Long id) {
    Notification notification = notificationRepository
      .findById(id)
      .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));

    return modelMapper.map(notification, NotificationDTO.class);
  }

  public List<NotificationDTO> getAll() {
    return notificationRepository
      .findAll()
      .stream()
      .map(n -> modelMapper.map(n, NotificationDTO.class))
      .collect(Collectors.toList());
  }

  public List<NotificationDTO> getByUser(String userEmail) {
    User user = userRepository
      .findByEmail(userEmail)
      .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    List<Notification> notifications = notificationRepository.findByUserUserIdAndIsReadFalse(
      user.getUserId()
    );
    return notifications
      .stream()
      .map(n -> modelMapper.map(n, NotificationDTO.class))
      .collect(Collectors.toList());
  }

  public NotificationDTO markAsRead(Long id) {
    Notification notification = notificationRepository
      .findById(id)
      .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));

    if (!notification.isRead()) {
      notification.setReadAt(LocalDateTime.now());
      notification.setRead(true);
    }

    return modelMapper.map(
      notificationRepository.save(notification),
      NotificationDTO.class
    );
  }

  public void delete(Long id) {
    if (!notificationRepository.existsById(id)) {
      throw new RuntimeException("Notificación no encontrada");
    }
    notificationRepository.deleteById(id);
  }
}
