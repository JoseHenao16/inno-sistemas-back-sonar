package com.udea.fe.service;

import com.udea.fe.DTO.UserDTO;
import com.udea.fe.entity.Role;
import com.udea.fe.entity.Status;
import com.udea.fe.entity.User;
import com.udea.fe.exception.UserException;
import com.udea.fe.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTestUpdate {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private ModelMapper modelMapper;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        modelMapper = mock(ModelMapper.class);
        userService = new UserService(userRepository, passwordEncoder, modelMapper);
    }

    @Test
    void updateUser_success() {
        UserDTO dto = new UserDTO(1L, "New Name", "new@mail.com", "999", "newpass", Role.TEACHER, Status.ACTIVE);
        User existing = new User();
        existing.setUserId(1L);
        existing.setName("Old Name");
        existing.setEmail("old@mail.com");
        existing.setDni("123");
        existing.setRole(Role.STUDENT);
        existing.setStatus(Status.ACTIVE);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.findByDni("999")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("new@mail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("newpass")).thenReturn("hashed");
        when(userRepository.save(any())).thenReturn(existing);
        when(modelMapper.map(any(User.class), eq(UserDTO.class))).thenReturn(dto);

        UserDTO result = userService.updateUser(1L, dto);

        assertEquals("New Name", result.getName());
        verify(userRepository).save(any());
    }

    @Test
    void updateUser_throwIfDniExists() {
        UserDTO dto = new UserDTO(1L, null, null, "999", null, null, null);
        User existing = new User();
        existing.setDni("123");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.findByDni("999")).thenReturn(Optional.of(new User()));

        Exception ex = assertThrows(UserException.class, () -> userService.updateUser(1L, dto));
        assertEquals("Ya existe otro usuario con el mismo DNI", ex.getMessage());
    }

    @Test
    void updateUser_throwIfEmailExists() {
        UserDTO dto = new UserDTO(1L, null, "new@mail.com", null, null, null, null);
        User existing = new User();
        existing.setEmail("old@mail.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.findByEmail("new@mail.com")).thenReturn(Optional.of(new User()));

        Exception ex = assertThrows(UserException.class, () -> userService.updateUser(1L, dto));
        assertEquals("Ya existe otro usuario con el mismo email", ex.getMessage());
    }

    @Test
    void updateUser_notFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        Exception ex = assertThrows(UserException.class, () -> userService.updateUser(1L, new UserDTO()));
        assertEquals("Usuario no encontrado", ex.getMessage());
    }

    @Test
    void updateUser_onlyPasswordEncoded() {
        UserDTO dto = new UserDTO();
        dto.setPassword("newpass");
        User existing = new User();
        existing.setPassword("oldpass");

        when(userRepository.findById(any())).thenReturn(Optional.of(existing));
        when(userRepository.findByDni(null)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(null)).thenReturn(Optional.empty());
        when(passwordEncoder.encode("newpass")).thenReturn("hashed");
        when(userRepository.save(any())).thenReturn(existing);
        when(modelMapper.map(any(), eq(UserDTO.class))).thenReturn(dto);

        UserDTO result = userService.updateUser(1L, dto);
        assertEquals("hashed", existing.getPassword());
    }
} 
