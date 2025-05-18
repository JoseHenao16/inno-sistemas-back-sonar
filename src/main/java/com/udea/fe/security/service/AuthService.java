package com.udea.fe.security.service;

import com.udea.fe.DTO.AuthResponse;
import com.udea.fe.DTO.LoginRequest;
import com.udea.fe.config.JwtService;
/* import com.udea.fe.entity.Status;
import com.udea.fe.entity.User;
import com.udea.fe.repository.UserRepository; */
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
/* import org.springframework.security.crypto.password.PasswordEncoder; */
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  /* private final PasswordEncoder passwordEncoder; */
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final UserDetailsServiceImpl userDetailsService;

  public AuthResponse login(LoginRequest request) {
    authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(
        request.getEmail(),
        request.getPassword()
      )
    );

    UserDetails userDetails = userDetailsService.loadUserByUsername(
      request.getEmail()
    );
    String token = jwtService.generateToken(userDetails.getUsername());
    return new AuthResponse(token);
  }
  /* public AuthResponse register(RegisterRequest request) {
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .dni(request.getDni())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .status(Status.ACTIVE)
                .build();

        userRepository.save(user);
        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token);
    } */
}
