package com.udea.fe.config;

import com.udea.fe.entity.Role;
import com.udea.fe.entity.Status;
import com.udea.fe.entity.User;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    private User user;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        // Simula valores inyectados desde application.properties
        ReflectionTestUtils.setField(jwtService, "secretKey", "12345678901234567890123456789012"); // 32 bytes
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 1000 * 60); // 1 minuto

        user = new User();
        user.setUserId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setDni("123456");
        user.setRole(Role.TEACHER);
        user.setStatus(Status.ACTIVE);
    }

    @Test
    void generateToken_and_extractUsername_success() {
        String token = jwtService.generateToken(user);
        assertNotNull(token);
        String username = jwtService.extractUsername(token);
        assertEquals(user.getEmail(), username);
    }

    @Test
    void isTokenValid_validToken_returnsTrue() {
        String token = jwtService.generateToken(user);
        boolean isValid = jwtService.isTokenValid(token, user.getEmail());
        assertTrue(isValid);
    }

    @Test
    void isTokenValid_wrongEmail_returnsFalse() {
        String token = jwtService.generateToken(user);
        boolean isValid = jwtService.isTokenValid(token, "wrong@example.com");
        assertFalse(isValid);
    }

    @Test
    void isTokenValid_expiredToken_returnsFalse() {
        // Generar un token expirado manualmente
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", -1000L); // token expirado desde el inicio

        String token = jwtService.generateToken(user);
        boolean isValid = jwtService.isTokenValid(token, user.getEmail());

        assertFalse(isValid);
    }

    @Test
    void extractClaim_returnsCorrectData() {
        String token = jwtService.generateToken(user);
        Date expiration = jwtService.extractClaim(token, claims -> claims.getExpiration());
        assertNotNull(expiration);
    }

    @Test
    void extractUsername_malformedToken_throwsException() {
        String fakeToken = "not.a.valid.token";
        assertThrows(Exception.class, () -> jwtService.extractUsername(fakeToken));
    }
}