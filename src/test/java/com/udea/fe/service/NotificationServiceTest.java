package com.udea.fe.service;

import com.udea.fe.DTO.NotificationDTO;
import com.udea.fe.entity.Notification;
import com.udea.fe.entity.User;
import com.udea.fe.exception.NotificationNotFoundException;
import com.udea.fe.exception.UserNotFoundException;
import com.udea.fe.repository.NotificationRepository;
import com.udea.fe.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    private NotificationRepository notificationRepository;
    private UserRepository userRepository;
    private ModelMapper modelMapper;
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationRepository = mock(NotificationRepository.class);
        userRepository = mock(UserRepository.class);
        modelMapper = mock(ModelMapper.class);
        notificationService = new NotificationService(notificationRepository, userRepository, modelMapper);
    }

    @Test
    void createNotification_success() {
        NotificationDTO dto = new NotificationDTO();
        dto.setUserId(1L);
        User user = new User();
        user.setUserId(1L);
        Notification entity = new Notification();
        entity.setUser(user);
        Notification saved = new Notification();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(modelMapper.map(dto, Notification.class)).thenReturn(entity);
        when(notificationRepository.save(any())).thenReturn(saved);
        when(modelMapper.map(saved, NotificationDTO.class)).thenReturn(dto);

        NotificationDTO result = notificationService.createNotification(dto);
        assertNotNull(result);
        verify(notificationRepository).save(any());
    }

    @Test
    void createNotification_userNotFound_throws() {
        NotificationDTO dto = new NotificationDTO();
        dto.setUserId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> notificationService.createNotification(dto));
    }

    @Test
    void getById_success() {
        Notification notification = new Notification();
        notification.setNotificationId(1L);
        NotificationDTO dto = new NotificationDTO();

        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        when(modelMapper.map(notification, NotificationDTO.class)).thenReturn(dto);

        NotificationDTO result = notificationService.getById(1L);
        assertNotNull(result);
    }

    @Test
    void getById_notFound_throws() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotificationNotFoundException.class, () -> notificationService.getById(1L));
    }

    @Test
    void getAll_success() {
        Notification notification = new Notification();
        NotificationDTO dto = new NotificationDTO();

        when(notificationRepository.findAll()).thenReturn(List.of(notification));
        when(modelMapper.map(any(Notification.class), eq(NotificationDTO.class))).thenReturn(dto);

        List<NotificationDTO> result = notificationService.getAll();
        assertEquals(1, result.size());
    }

    @Test
    void getByUser_success() {
        User user = new User();
        user.setUserId(1L);
        Notification notification = new Notification();
        NotificationDTO dto = new NotificationDTO();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(notificationRepository.findByUserUserIdAndIsReadFalse(1L)).thenReturn(List.of(notification));
        when(modelMapper.map(any(Notification.class), eq(NotificationDTO.class))).thenReturn(dto);

        List<NotificationDTO> result = notificationService.getByUser("test@example.com");
        assertEquals(1, result.size());
    }

    @Test
    void getByUser_userNotFound_throws() {
        when(userRepository.findByEmail("fail@example.com")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> notificationService.getByUser("fail@example.com"));
    }

    @Test
    void markAsRead_success() {
        Notification notification = new Notification();
        notification.setNotificationId(1L);
        notification.setRead(false);
        NotificationDTO dto = new NotificationDTO();

        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(notification)).thenReturn(notification);
        when(modelMapper.map(notification, NotificationDTO.class)).thenReturn(dto);

        NotificationDTO result = notificationService.markAsRead(1L);
        assertNotNull(result);
        assertTrue(notification.isRead());
    }

    @Test
    void markAsRead_notFound_throws() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotificationNotFoundException.class, () -> notificationService.markAsRead(1L));
    }

    @Test
    void delete_success() {
        when(notificationRepository.existsById(1L)).thenReturn(true);
        doNothing().when(notificationRepository).deleteById(1L);

        notificationService.delete(1L);

        verify(notificationRepository).deleteById(1L);
    }

    @Test
    void delete_notFound_throws() {
        when(notificationRepository.existsById(1L)).thenReturn(false);
        assertThrows(NotificationNotFoundException.class, () -> notificationService.delete(1L));
    }
}