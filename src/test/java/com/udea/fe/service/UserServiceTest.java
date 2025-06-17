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
        modelMapper = mock(ModelMapper.class); // usa mock, no instancia real
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
        when(userRepository.existsById(any())).thenReturn(false);
        when(passwordEncoder.encode("pass")).thenReturn("encrypted");
        when(modelMapper.map(any(UserDTO.class), eq(User.class))).thenReturn(user);
        when(modelMapper.map(any(User.class), eq(UserDTO.class))).thenReturn(dto);
        when(userRepository.save(any())).thenReturn(user);

        UserDTO result = userService.createUser(dto);

        assertEquals(dto.getDni(), result.getDni());
        assertEquals(dto.getEmail(), result.getEmail());
        assertEquals(dto.getName(), result.getName());
        assertEquals(dto.getRole(), result.getRole());
    }

    @Test
    void getUserByID_success() {
        User user = new User();
        user.setUserId(1L);
        user.setName("Ana");
        user.setEmail("ana@mail.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(modelMapper.map(any(User.class), eq(UserDTO.class)))
                .thenReturn(new UserDTO(1L, "Ana", "ana@mail.com", null, null, Role.STUDENT, Status.ACTIVE));

        UserDTO dto = userService.getUserByID(1L);
        assertEquals("Ana", dto.getName());
    }

    @Test
    void getUserByID_notFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        Exception ex = assertThrows(UserException.class, () -> userService.getUserByID(99L));
        assertEquals("Usuario no encontrado", ex.getMessage());
    }

    @Test
    void getAllUsers_success() {
        User u1 = new User();
        u1.setName("A");
        u1.setRole(Role.STUDENT);
        User u2 = new User();
        u2.setName("B");
        u2.setRole(Role.TEACHER);

        when(userRepository.findByRoleNot(Role.ADMIN)).thenReturn(List.of(u1, u2));
        when(modelMapper.map(eq(u1), eq(UserDTO.class)))
                .thenReturn(new UserDTO(null, "A", null, null, null, Role.STUDENT, null));
        when(modelMapper.map(eq(u2), eq(UserDTO.class)))
                .thenReturn(new UserDTO(null, "B", null, null, null, Role.TEACHER, null));

        List<UserDTO> users = userService.getAllUsers();
        assertEquals(2, users.size());
    }

    @Test
    void deleteUser_success() {
        User user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteById(1L);
        userService.deleteUser(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_notFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        Exception ex = assertThrows(UserException.class, () -> userService.deleteUser(2L));
        assertEquals("Usuario no encontrado", ex.getMessage());
    }

    @Test
    void deactivateUser_success() {
        User user = new User();
        user.setStatus(Status.ACTIVE);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);
        userService.deactivateUser(1L);
        assertEquals(Status.INACTIVE, user.getStatus());
    }

    @Test
    void deactivateUser_notFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        Exception ex = assertThrows(UserException.class, () -> userService.deactivateUser(1L));
        assertEquals("Usuario no encontrado", ex.getMessage());
    }
}
