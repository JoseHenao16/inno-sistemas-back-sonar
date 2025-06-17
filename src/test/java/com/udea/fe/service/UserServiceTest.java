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
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
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
        modelMapper = new ModelMapper();
        userService = new UserService(userRepository, passwordEncoder, modelMapper);
    }

    @Test
    void createUser_success() {
        UserDTO dto = new UserDTO(null, "Juan", "juan@mail.com", "123", "pass", Role.STUDENT, null);
        when(userRepository.findByDni("123")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("juan@mail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass")).thenReturn("encrypted");
        when(userRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        UserDTO result = userService.createUser(dto);

        assertEquals("Juan", result.getName());
        assertEquals("juan@mail.com", result.getEmail());
        assertEquals("123", result.getDni());
        assertEquals(Role.STUDENT, result.getRole());
        assertEquals(Status.ACTIVE, result.getStatus());
    }

    @Test
    void createUser_throwsIfDniExists() {
        UserDTO dto = new UserDTO(null, "Juan", "juan@mail.com", "123", "pass", Role.STUDENT, null);
        when(userRepository.findByDni("123")).thenReturn(Optional.of(new User()));
        assertThrows(UserException.class, () -> userService.createUser(dto));
    }

    @Test
    void createUser_throwsIfEmailExists() {
        UserDTO dto = new UserDTO(null, "Juan", "juan@mail.com", "123", "pass", Role.STUDENT, null);
        when(userRepository.findByDni("123")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("juan@mail.com")).thenReturn(Optional.of(new User()));
        assertThrows(UserException.class, () -> userService.createUser(dto));
    }

    @Test
    void createUser_throwsIfIdExists() {
        UserDTO dto = new UserDTO(1L, "Juan", "juan@mail.com", "123", "pass", Role.STUDENT, null);
        when(userRepository.findByDni("123")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("juan@mail.com")).thenReturn(Optional.empty());
        when(userRepository.existsById(1L)).thenReturn(true);
        assertThrows(UserException.class, () -> userService.createUser(dto));
    }

    @Test
    void getUserByID_success() {
        User user = new User();
        user.setUserId(1L);
        user.setName("Ana");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        UserDTO dto = userService.getUserByID(1L);
        assertEquals("Ana", dto.getName());
    }

    @Test
    void getUserByID_notFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(UserException.class, () -> userService.getUserByID(2L));
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
        when(userRepository.findByRoleNot(Role.ADMIN)).thenReturn(List.of(user1, user2));
        List<UserDTO> users = userService.getAllUsers();
        assertEquals(2, users.size());
        assertEquals("A", users.get(0).getName());
        assertEquals("B", users.get(1).getName());
    }

    @Test
    void updateUser_success() {
        User existing = new User();
        existing.setUserId(1L);
        existing.setName("Old");
        existing.setEmail("old@mail.com");
        existing.setDni("111");
        existing.setRole(Role.STUDENT);
        existing.setStatus(Status.ACTIVE);
        existing.setPassword("oldpass");

        UserDTO dto = new UserDTO(null, "New", "new@mail.com", "222", "newpass", Role.TEACHER, Status.INACTIVE);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.findByDni("222")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("new@mail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("newpass")).thenReturn("encNew");
        when(userRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        UserDTO result = userService.updateUser(1L, dto);
        assertEquals("New", result.getName());
        assertEquals("new@mail.com", result.getEmail());
        assertEquals("222", result.getDni());
        assertEquals(Role.TEACHER, result.getRole());
        assertEquals(Status.INACTIVE, result.getStatus());
    }

    @Test
    void updateUser_throwsIfNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        UserDTO dto = new UserDTO();
        assertThrows(UserException.class, () -> userService.updateUser(1L, dto));
    }

    @Test
    void updateUser_throwsIfDniExists() {
        User existing = new User();
        existing.setUserId(1L);
        existing.setDni("111");
        UserDTO dto = new UserDTO(null, null, null, "222", null, null, null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.findByDni("222")).thenReturn(Optional.of(new User()));
        assertThrows(UserException.class, () -> userService.updateUser(1L, dto));
    }

    @Test
    void updateUser_throwsIfEmailExists() {
        User existing = new User();
        existing.setUserId(1L);
        existing.setEmail("old@mail.com");
        UserDTO dto = new UserDTO(null, null, "new@mail.com", null, null, null, null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.findByEmail("new@mail.com")).thenReturn(Optional.of(new User()));
        assertThrows(UserException.class, () -> userService.updateUser(1L, dto));
    }

    @Test
    void updateUser_passwordNotUpdatedIfBlank() {
        User existing = new User();
        existing.setUserId(1L);
        existing.setPassword("oldpass");
        UserDTO dto = new UserDTO(null, null, null, null, "   ", null, null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        UserDTO result = userService.updateUser(1L, dto);
        assertEquals("oldpass", existing.getPassword());
    }

    @Test
    void deleteUser_success() {
        User user = new User();
        user.setUserId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteById(1L);
        userService.deleteUser(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_notFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserException.class, () -> userService.deleteUser(1L));
    }

    @Test
    void deactivateUser_success() {
        User user = new User();
        user.setUserId(1L);
        user.setStatus(Status.ACTIVE);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        userService.deactivateUser(1L);
        assertEquals(Status.INACTIVE, user.getStatus());
    }

    @Test
    void deactivateUser_notFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserException.class, () -> userService.deactivateUser(1L));
    }
}