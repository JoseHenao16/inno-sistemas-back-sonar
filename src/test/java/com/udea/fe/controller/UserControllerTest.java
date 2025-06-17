package com.udea.fe.controller;

import com.udea.fe.DTO.UserDTO;
import com.udea.fe.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser() {
        UserDTO input = new UserDTO();
        input.setName("testuser");

        UserDTO expected = new UserDTO();
        expected.setName("testuser");

        when(userService.createUser(input)).thenReturn(expected);

        ResponseEntity<UserDTO> response = userController.createUser(input);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(expected.getName(), response.getBody().getName());
    }

    @Test
    void testGetAllUsers() {
        UserDTO user1 = new UserDTO();
        user1.setName("user1");

        UserDTO user2 = new UserDTO();
        user2.setName("user2");

        List<UserDTO> mockList = Arrays.asList(user1, user2);

        when(userService.getAllUsers()).thenReturn(mockList);

        ResponseEntity<Iterable<UserDTO>> response = userController.getAllUsers();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockList, response.getBody());
    }

    @Test
    void testGetUserById() {
        Long id = 1L;
        UserDTO user = new UserDTO();
        user.setName("user");

        when(userService.getUserByID(id)).thenReturn(user);

        ResponseEntity<UserDTO> response = userController.getUserById(id);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("user", response.getBody().getName());
    }

    @Test
    void testUpdateUser() {
        Long id = 1L;
        UserDTO input = new UserDTO();
        input.setName("updated");

        UserDTO updated = new UserDTO();
        updated.setName("updated");

        when(userService.updateUser(id, input)).thenReturn(updated);

        ResponseEntity<UserDTO> response = userController.updateUser(id, input);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("updated", response.getBody().getName());
    }

    @Test
    void testDeactivateUser() {
        Long id = 1L;

        doNothing().when(userService).deactivateUser(id);

        ResponseEntity<Void> response = userController.deactivateUser(id);

        assertEquals(204, response.getStatusCodeValue());
        verify(userService, times(1)).deactivateUser(id);
    }
}