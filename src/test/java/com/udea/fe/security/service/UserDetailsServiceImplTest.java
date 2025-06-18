package com.udea.fe.security.service;

import com.udea.fe.entity.Role;
import com.udea.fe.entity.Status;
import com.udea.fe.entity.User;
import com.udea.fe.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserDetailsServiceImplTest {

    private UserRepository userRepository;
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userDetailsService = new UserDetailsServiceImpl(userRepository);
    }

    @Test
    void loadUserByUsername_userFound_returnsUserDetails() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("securepass");
        user.setStatus(Status.ACTIVE);
        user.setRole(Role.TEACHER); // Asegúrate que este rol tenga getAuthorities()

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("test@example.com");

        // Assert
        assertEquals("test@example.com", userDetails.getUsername());
        assertEquals("securepass", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertNotNull(userDetails.getAuthorities());
    }

    @Test
    void loadUserByUsername_userNotFound_throwsException() {
        // Arrange
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("notfound@example.com")
        );

        assertEquals("Usuario no encontrado con email: notfound@example.com", exception.getMessage());
    }
}