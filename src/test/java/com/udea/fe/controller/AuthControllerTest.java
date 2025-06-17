package com.udea.fe.controller;

import com.udea.fe.DTO.AuthResponse;
import com.udea.fe.DTO.LoginRequest;
import com.udea.fe.DTO.UserDTO;
import com.udea.fe.entity.User;
import com.udea.fe.repository.UserRepository;
import com.udea.fe.security.service.AuthService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loginSuccess() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");

        AuthResponse response = new AuthResponse();
        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        ResponseEntity<AuthResponse> result = authController.login(request);

        assertEquals(200, result.getStatusCodeValue());
        assertNotNull(result.getBody());
    }

    @Test
    void loginFailure() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("wrong");

        when(authService.login(any(LoginRequest.class))).thenThrow(new RuntimeException("Login failed"));

        ResponseEntity<AuthResponse> result = authController.login(request);

        assertEquals(401, result.getStatusCodeValue());
    }

    @Test
    void getCurrentUser_Success() {
        org.springframework.security.core.userdetails.User springUser =
                new org.springframework.security.core.userdetails.User("user@example.com", "pass", new java.util.ArrayList<>());

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(springUser);

        User userEntity = new User();
        userEntity.setUserId(1L);
        userEntity.setEmail("user@example.com");
        userEntity.setFullName("Nombre Apellido");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(userEntity));

        ResponseEntity<UserDTO> result = authController.getCurrentUser(authentication);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals("user@example.com", result.getBody().getEmail());
    }

    @Test
    void getCurrentUser_NotAuthenticated() {
        when(authentication.isAuthenticated()).thenReturn(false);

        ResponseEntity<UserDTO> result = authController.getCurrentUser(authentication);
        assertEquals(401, result.getStatusCodeValue());
    }

    @Test
    void getCurrentUser_NotUserDetails() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("una cadena rara");

        ResponseEntity<UserDTO> result = authController.getCurrentUser(authentication);
        assertEquals(401, result.getStatusCodeValue());
    }
}