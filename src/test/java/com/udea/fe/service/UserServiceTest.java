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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class UserServiceTest {

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
    void createUser_success() {
        UserDTO dto = new UserDTO(null, "Juan", "juan@mail.com", "123", "pass", Role.STUDENT, null);
        User user = new User();
        user.setPassword("encrypted");
        user.setRole(Role.STUDENT);
        user.setDni("123");
        user.setEmail("juan@mail.com");
        user.setName("Juan");
        user.setStatus(Status.ACTIVE);

        when(userRepository.findByDni("123")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("juan@mail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass")).thenReturn("encrypted");
        when(modelMapper.map(any(UserDTO.class), eq(User.class))).thenReturn(user);
        when(modelMapper.map(any(User.class), eq(UserDTO.class))).thenReturn(dto);
        when(userRepository.save(any())).thenReturn(user);

        UserDTO result = userService.createUser(dto);

        assertEquals(dto.getDni(), result.getDni());
    }

    @Test
    void createUser_duplicateDni() {
        UserDTO dto = new UserDTO(null, "Juan", "juan@mail.com", "123", "pass", Role.STUDENT, Status.ACTIVE);

        when(userRepository.findByDni("123")).thenReturn(Optional.of(new User()));

        Exception ex = assertThrows(UserException.class, () -> userService.createUser(dto));
        assertEquals("Ya existe un usuario con el DNI proporcionado", ex.getMessage());
    }

    @Test
    void createUser_duplicateEmail() {
        UserDTO dto = new UserDTO(null, "Juan", "juan@mail.com", "123", "pass", Role.STUDENT, Status.ACTIVE);

        when(userRepository.findByDni("123")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("juan@mail.com")).thenReturn(Optional.of(new User()));

        Exception ex = assertThrows(UserException.class, () -> userService.createUser(dto));
        assertEquals("Ya existe un usuario con el email proporcionado", ex.getMessage());
    }

    @Test
    void createUser_duplicateId() {
        UserDTO dto = new UserDTO(10L, "Juan", "juan@mail.com", "123", "pass", Role.STUDENT, Status.ACTIVE);

        when(userRepository.findByDni("123")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("juan@mail.com")).thenReturn(Optional.empty());
        when(userRepository.existsById(10L)).thenReturn(true);

        Exception ex = assertThrows(UserException.class, () -> userService.createUser(dto));
        assertEquals("Ya existe un usuario con el ID proporcionado", ex.getMessage());
    }

    @Test
    void updateUser_success() {
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setUserId(userId);
        existingUser.setDni("123");
        existingUser.setEmail("original@mail.com");

        UserDTO dto = new UserDTO(userId, "Nuevo Nombre", "nuevo@mail.com", "456", "nuevaPass", Role.TEACHER,
                Status.INACTIVE);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByDni("456")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("nuevo@mail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("nuevaPass")).thenReturn("hashedPass");
        when(userRepository.save(any())).thenReturn(existingUser);
        when(modelMapper.map(any(User.class), eq(UserDTO.class))).thenReturn(dto);

        UserDTO result = userService.updateUser(userId, dto);

        assertEquals("Nuevo Nombre", result.getName());
        assertEquals("456", existingUser.getDni());
        assertEquals("nuevo@mail.com", existingUser.getEmail());
        assertEquals(Status.INACTIVE, existingUser.getStatus());
        assertEquals(Role.TEACHER, existingUser.getRole());
        assertEquals("hashedPass", existingUser.getPassword());
    }

    @Test
    void updateUser_duplicateDni_throws() {
        UserDTO dto = new UserDTO(null, "A", "same@mail.com", "duplicate", null, null, null);
        User user = new User();
        user.setDni("old");
        user.setEmail("same@mail.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByDni("duplicate")).thenReturn(Optional.of(new User()));
        assertThrows(UserException.class, () -> userService.updateUser(1L, dto));
    }

    @Test
    void updateUser_duplicateEmail_throws() {
        UserDTO dto = new UserDTO(null, "A", "new@mail.com", "old", null, null, null);
        User user = new User();
        user.setDni("old");
        user.setEmail("old@mail.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("new@mail.com")).thenReturn(Optional.of(new User()));
        assertThrows(UserException.class, () -> userService.updateUser(1L, dto));
    }

    @Test
    void getUserByID_notFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(UserException.class, () -> userService.getUserByID(99L));
    }

    @Test
    void deleteUser_notFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserException.class, () -> userService.deleteUser(1L));
    }

    @Test
    void deactivateUser_success() {
        User user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        userService.deactivateUser(1L);
        assertEquals(Status.INACTIVE, user.getStatus());
    }

    @Test
    void updateUser_duplicateDni_throwsException() {
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setUserId(userId);
        existingUser.setDni("123");

        UserDTO dto = new UserDTO(userId, "Nombre", "mail@mail.com", "456", null, Role.STUDENT, null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByDni("456")).thenReturn(Optional.of(new User()));

        Exception ex = assertThrows(UserException.class, () -> userService.updateUser(userId, dto));
        assertEquals("Ya existe otro usuario con el mismo DNI", ex.getMessage());
    }

    @Test
    void updateUser_duplicateEmail_throwsException() {
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setUserId(userId);
        existingUser.setEmail("original@mail.com");

        UserDTO dto = new UserDTO(userId, "Nombre", "nuevo@mail.com", "123", null, Role.STUDENT, null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByDni("123")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("nuevo@mail.com")).thenReturn(Optional.of(new User()));

        Exception ex = assertThrows(UserException.class, () -> userService.updateUser(userId, dto));
        assertEquals("Ya existe otro usuario con el mismo email", ex.getMessage());
    }

    @Test
    void updateUser_userNotFound_throwsException() {
        Long userId = 999L;
        UserDTO dto = new UserDTO(userId, "Nombre", "mail@mail.com", "123", null, Role.STUDENT, null);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Exception ex = assertThrows(UserException.class, () -> userService.updateUser(userId, dto));
        assertEquals("Usuario no encontrado", ex.getMessage());
    }

}