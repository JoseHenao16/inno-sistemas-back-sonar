package com.udea.fe.controller;

import com.udea.fe.DTO.AuthResponse;
import com.udea.fe.DTO.LoginRequest;
import com.udea.fe.DTO.UserDTO;
import com.udea.fe.entity.User;
import com.udea.fe.repository.UserRepository;
import com.udea.fe.security.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthControllerTest {

    private AuthController authController;
    private AuthService authService;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        authService = mock(AuthService.class);
        userRepository = mock(UserRepository.class);
        authController = new AuthController(authService, userRepository);
    }

    @Test
    void login_ReturnsAuthResponse_WhenValidRequest() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");

        AuthResponse expectedResponse = new AuthResponse();
        when(authService.login(request)).thenReturn(expectedResponse);

        ResponseEntity<AuthResponse> response = authController.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void login_Returns401_WhenExceptionThrown() {
        LoginRequest request = new LoginRequest();
        request.setEmail("fail@example.com");
        request.setPassword("password");

        when(authService.login(request)).thenThrow(new RuntimeException("Login failed"));

        ResponseEntity<AuthResponse> response = authController.login(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void getCurrentUser_Returns401_WhenAuthenticationIsNull() {
        ResponseEntity<UserDTO> response = authController.getCurrentUser(null);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void getCurrentUser_Returns401_WhenAuthenticationNotAuthenticated() {
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(false);

        ResponseEntity<UserDTO> response = authController.getCurrentUser(auth);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void getCurrentUser_Returns404_WhenUserNotFound() {
        String email = "notfound@example.com";

        Authentication auth = mock(Authentication.class);
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(email, "pass",
                Collections.emptyList());

        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        ResponseEntity<UserDTO> response = authController.getCurrentUser(auth);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getCurrentUser_Returns401_WhenPrincipalNotUserDetails() {
        Authentication auth = mock(Authentication.class);

        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn("not a user details object");

        ResponseEntity<UserDTO> response = authController.getCurrentUser(auth);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void getCurrentUser_Returns200_WhenUserFound() {
        String email = "user@example.com";

        Authentication auth = mock(Authentication.class);
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(email, "pass",
                Collections.emptyList());

        User mockUser = new User();
        mockUser.setEmail(email);

        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        ResponseEntity<UserDTO> response = authController.getCurrentUser(auth);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(email, response.getBody().getEmail());
    }
}
