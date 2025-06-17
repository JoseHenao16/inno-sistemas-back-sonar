package com.udea.fe.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserFields() {
        User user = new User();
        user.setUserId(1L);
        user.setName("Juan Pérez");
        user.setEmail("juan@example.com");
        user.setDni("12345678");
        user.setPassword("secure123");
        user.setRole(Role.STUDENT);
        user.setCreatedAt(LocalDateTime.of(2024, 1, 1, 10, 0));
        user.setStatus(Status.ACTIVE);

        assertEquals(1L, user.getUserId());
        assertEquals("Juan Pérez", user.getName());
        assertEquals("juan@example.com", user.getEmail());
        assertEquals("12345678", user.getDni());
        assertEquals("secure123", user.getPassword());
        assertEquals(Role.STUDENT, user.getRole());
        assertEquals(LocalDateTime.of(2024, 1, 1, 10, 0), user.getCreatedAt());
        assertEquals(Status.ACTIVE, user.getStatus());
    }

    @Test
    void testSetFullName() {
        User user = new User();
        user.setFullName("Carlos Gómez");
        assertEquals("Carlos Gómez", user.getName());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        User user = new User(
                2L,
                "Ana Torres",
                "ana@example.com",
                "87654321",
                "pass123",
                Role.TEACHER,
                now,
                Status.INACTIVE
        );

        assertEquals(2L, user.getUserId());
        assertEquals("Ana Torres", user.getName());
        assertEquals("ana@example.com", user.getEmail());
        assertEquals("87654321", user.getDni());
        assertEquals("pass123", user.getPassword());
        assertEquals(Role.TEACHER, user.getRole());
        assertEquals(now, user.getCreatedAt());
        assertEquals(Status.INACTIVE, user.getStatus());
    }
}
