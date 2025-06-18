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

import java.time.LocalDateTime;
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
    void createUser_setsCreatedAtAndStatusIfNull() {
        UserDTO dto = new UserDTO(null, "Ana", "ana@mail.com", "321", "clave", Role.TEACHER, null);
        User user = new User();
        user.setPassword("encrypted");
        user.setRole(Role.TEACHER);
        user.setDni("321");
        user.setEmail("ana@mail.com");
        user.setName("Ana");
        user.setStatus(null);
        user.setCreatedAt(null);

        when(userRepository.findByDni("321")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("ana@mail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("clave")).thenReturn("encrypted");
        when(modelMapper.map(any(UserDTO.class), eq(User.class))).thenReturn(user);
        when(modelMapper.map(any(User.class), eq(UserDTO.class))).thenReturn(dto);
        when(userRepository.save(any())).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setCreatedAt(LocalDateTime.now());
            u.setStatus(Status.ACTIVE);
            return u;
        });

        UserDTO result = userService.createUser(dto);

        assertNotNull(user.getCreatedAt());
        assertEquals(Status.ACTIVE, user.getStatus());
        assertEquals(dto.getDni(), result.getDni());
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
    void getUserByID_success() {
        User user = new User();
        user.setUserId(1L);
        user.setName("Carlos");
        UserDTO dto = new UserDTO(1L, "Carlos", "carlos@mail.com", "999", "pass", Role.STUDENT, Status.ACTIVE);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserDTO.class)).thenReturn(dto);

        UserDTO result = userService.getUserByID(1L);
        assertEquals("Carlos", result.getName());
    }

    @Test
    void getUserByID_notFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(UserException.class, () -> userService.getUserByID(99L));
    }

    @Test
    void getAllUsers_success() {
        User user1 = new User();
        user1.setUserId(1L);
        user1.setName("A");
        user1.setRole(Role.STUDENT);
        User user2 = new User();
        user2.setUserId(2L);
        user2.setName("B");
        user2.setRole(Role.TEACHER);

        UserDTO dto1 = new UserDTO(1L, "A", "a@mail.com", "111", null, Role.STUDENT, Status.ACTIVE);
        UserDTO dto2 = new UserDTO(2L, "B", "b@mail.com", "222", null, Role.TEACHER, Status.ACTIVE);

        when(userRepository.findByRoleNot(Role.ADMIN)).thenReturn(List.of(user1, user2));
        when(modelMapper.map(user1, UserDTO.class)).thenReturn(dto1);
        when(modelMapper.map(user2, UserDTO.class)).thenReturn(dto2);

        List<UserDTO> users = userService.getAllUsers();
        assertEquals(2, users.size());
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
    void updateUser_userNotFound_throwsException() {
        Long userId = 999L;
        UserDTO dto = new UserDTO(userId, "Nombre", "mail@mail.com", "123", null, Role.STUDENT, null);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Exception ex = assertThrows(UserException.class, () -> userService.updateUser(userId, dto));
        assertEquals("Usuario no encontrado", ex.getMessage());
    }

    @Test
    void deleteUser_success() {
        User user = new User();
        user.setUserId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_userNotFound_throwsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(UserException.class, () -> userService.deleteUser(1L));
        assertEquals("Usuario no encontrado", ex.getMessage());
    }

    @Test
    void deactivateUser_success() {
        User user = new User();
        user.setUserId(1L);
        user.setStatus(Status.ACTIVE);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        userService.deactivateUser(1L);

        assertEquals(Status.INACTIVE, user.getStatus());
        verify(userRepository).save(user);
    }

    @Test
    void deactivateUser_userNotFound_throwsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(UserException.class, () -> userService.deactivateUser(1L));
        assertEquals("Usuario no encontrado", ex.getMessage());
    }

    @Test
    void updateUser_cambioDniYaExistente_throwsException() {
        UserDTO dto = new UserDTO();
        dto.setDni("nuevoDNI");

        User existingUser = new User();
        existingUser.setDni("viejoDNI");
        existingUser.setEmail("same@mail.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByDni("nuevoDNI")).thenReturn(Optional.of(new User()));

        Exception ex = assertThrows(UserException.class, () -> userService.updateUser(1L, dto));
        assertEquals("Ya existe otro usuario con el mismo DNI", ex.getMessage());
    }
}
