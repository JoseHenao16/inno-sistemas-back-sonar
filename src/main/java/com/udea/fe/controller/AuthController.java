package com.udea.fe.controller;

import com.udea.fe.DTO.AuthResponse;
import com.udea.fe.DTO.LoginRequest;
import com.udea.fe.DTO.UserDTO;
import com.udea.fe.entity.User;
import com.udea.fe.repository.UserRepository;
import com.udea.fe.security.service.AuthService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;
  private final UserRepository userRepository;

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

  @GetMapping("/me")
public ResponseEntity<UserDTO> getCurrentUser(Authentication authentication) {
    System.out.println("\n ---> Obtener usuario autenticado: ");
    if (authentication == null || !authentication.isAuthenticated()) {
      System.out.println("Usuario no autenticado");
      return ResponseEntity.status(401).build();
    }

    Object principal = authentication.getPrincipal();

    System.out.println("Principal: " + principal);
    System.out.println("Tipo de principal: " + principal.getClass());

    if (principal instanceof org.springframework.security.core.userdetails.User) {
      String username = ((org.springframework.security.core.userdetails.User) principal).getUsername();

      User userEntity = userRepository.findByEmail(username).orElse(null);

      if (userEntity == null) {
          return ResponseEntity.status(404).build();
      }

      UserDTO userDto = new UserDTO(userEntity);
      System.out.println("Usuario autenticado: " + userDto);
      return ResponseEntity.ok(userDto);
    } else {
      return ResponseEntity.status(401).build();
    }
}

}
