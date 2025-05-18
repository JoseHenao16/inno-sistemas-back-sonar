package com.udea.fe.controller;
import com.udea.fe.DTO.UserDTO;
import com.udea.fe.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor

public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        UserDTO createdUser = userService.createUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping("/all")
    public ResponseEntity<Iterable<UserDTO>> getAllUsers() {
        Iterable<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO user = userService.getUserByID(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}/edit")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UserDTO userDTO) {

        UserDTO updatedUser = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(updatedUser);
    }


    @PutMapping("/{id}/status")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }

}
