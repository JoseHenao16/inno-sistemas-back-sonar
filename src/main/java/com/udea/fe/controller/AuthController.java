package com.udea.fe.controller;

import com.udea.fe.DTO.AuthResponse;
import com.udea.fe.DTO.LoginRequest;
import com.udea.fe.DTO.UserDTO;
import com.udea.fe.security.service.AuthService;
import com.udea.fe.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

  private final AuthService authService;
  private final UserService userService;

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
    try {
      System.out.println(
        "\n ---> Login recibido para usuario: " + request.getEmail() + "\n"
      );
      AuthResponse response = authService.login(request);
      System.out.println("Login exitoso, generando respuesta");
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      System.out.println("Error en login: " + e.getMessage());
      e.printStackTrace();
      return ResponseEntity.status(401).build();
    }
  }

  /* @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    } */

  @PostMapping("/register")
  public ResponseEntity<UserDTO> createUser(
    @Valid @RequestBody UserDTO userDTO
  ) {
    UserDTO createdUser = userService.createUser(userDTO);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
  }
}
