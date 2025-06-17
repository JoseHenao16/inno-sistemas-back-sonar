package com.udea.fe.controller;

import com.udea.fe.DTO.NotificationDTO;
import com.udea.fe.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private Principal principal;

    @InjectMocks
    private NotificationController notificationController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetById() {
        Long id = 1L;
        NotificationDTO mockNotification = new NotificationDTO();
        when(notificationService.getById(id)).thenReturn(mockNotification);

        ResponseEntity<NotificationDTO> response = notificationController.getById(id);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockNotification, response.getBody());
    }

    @Test
    public void testGetByUserId() {
        String userEmail = "test@example.com";
        List<NotificationDTO> mockList = Arrays.asList(new NotificationDTO(), new NotificationDTO());

        when(principal.getName()).thenReturn(userEmail);
        when(notificationService.getByUser(userEmail)).thenReturn(mockList);

        ResponseEntity<List<NotificationDTO>> response = notificationController.getByUserId(principal);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockList, response.getBody());
    }

    @Test
    public void testDelete() {
        Long id = 1L;

        ResponseEntity<Void> response = notificationController.delete(id);

        assertEquals(204, response.getStatusCodeValue());
        verify(notificationService, times(1)).delete(id);
    }

    @Test
    public void testMarkAsRead() {
        Long id = 1L;
        NotificationDTO markedAsRead = new NotificationDTO();
        when(notificationService.markAsRead(id)).thenReturn(markedAsRead);

        ResponseEntity<NotificationDTO> response = notificationController.markAsRead(id);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(markedAsRead, response.getBody());
    }
}
