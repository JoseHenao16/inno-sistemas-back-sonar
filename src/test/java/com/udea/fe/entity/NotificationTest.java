package com.udea.fe.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class NotificationTest {

    @Test
    void testAllArgsConstructor() {
        User user = new User(); // puedes simular con un objeto vacío
        LocalDateTime now = LocalDateTime.now();

        Notification notification = new Notification(
                1L,
                user,
                "Mensaje de prueba",
                "INFO",
                true,
                now,
                now.plusMinutes(1)
        );

        assertEquals(1L, notification.getNotificationId());
        assertEquals(user, notification.getUser());
        assertEquals("Mensaje de prueba", notification.getMessage());
        assertEquals("INFO", notification.getType());
        assertTrue(notification.isRead());
        assertEquals(now, notification.getCreatedAt());
        assertEquals(now.plusMinutes(1), notification.getReadAt());
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        Notification notification = new Notification();
        User user = new User();
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime read = created.plusMinutes(2);

        notification.setNotificationId(2L);
        notification.setUser(user);
        notification.setMessage("Otra prueba");
        notification.setType("ALERT");
        notification.setRead(false);
        notification.setCreatedAt(created);
        notification.setReadAt(read);

        assertEquals(2L, notification.getNotificationId());
        assertEquals(user, notification.getUser());
        assertEquals("Otra prueba", notification.getMessage());
        assertEquals("ALERT", notification.getType());
        assertFalse(notification.isRead());
        assertEquals(created, notification.getCreatedAt());
        assertEquals(read, notification.getReadAt());
    }

    @Test
    void testPrePersistSetsCreatedAt() {
        Notification notification = new Notification();
        notification.prePersist();
        assertNotNull(notification.getCreatedAt());
    }
}
