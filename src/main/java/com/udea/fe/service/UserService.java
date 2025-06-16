package com.udea.fe.service;

import com.udea.fe.DTO.UserDTO;
import com.udea.fe.entity.Role;
import com.udea.fe.entity.Status;
import com.udea.fe.entity.User;
import com.udea.fe.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@AllArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final ModelMapper modelMapper;

  public UserDTO createUser(UserDTO userDTO) {
    //existencia de usuarios con mismos dni
    if (userRepository.findByDni(userDTO.getDni()).isPresent()) {
      throw new RuntimeException(
        "Ya existe un usuario con el DNI proporcionado"
      );
    }

    //existencia de usuarios con mismos correos
    if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
      throw new RuntimeException(
        "Ya existe un usuario con el email proporcionado"
      );
    }

    //existencia de usuarios con mismos IDs
    if (userDTO.getId() != null && userRepository.existsById(userDTO.getId())) {
      throw new RuntimeException(
        "Ya existe un usuario con el ID proporcionado"
      );
    }

    User user = modelMapper.map(userDTO, User.class);
    user.setPassword(passwordEncoder.encode(user.getPassword()));

    //asignar fecha si ingresa nula
    if (user.getCreatedAt() == null) {
      user.setCreatedAt(LocalDateTime.now());
    }

    if (user.getStatus() == null) {
      user.setStatus(Status.ACTIVE);
    }
    User savedUser = userRepository.save(user);
    return modelMapper.map(savedUser, UserDTO.class);
  }

  public UserDTO getUserByID(Long id) {
    return userRepository
      .findById(id)
      .map(user -> modelMapper.map(user, UserDTO.class))
      .orElseThrow(() -> new RuntimeException("User not found"));
  }

  public List<UserDTO> getAllUsers() {
    return userRepository
      .findByRoleNot(Role.ADMIN)
      .stream()
      .map(user -> modelMapper.map(user, UserDTO.class))
      .collect(Collectors.toList());
  }

  public UserDTO updateUser(Long id, UserDTO userDTO) {
    return userRepository
      .findById(id)
      .map(existingUser -> {
        // validar si se quiere cambiar el DNI a uno que ya existe en otro usuario
        if (
          userDTO.getDni() != null &&
          !userDTO.getDni().equals(existingUser.getDni()) &&
          userRepository.findByDni(userDTO.getDni()).isPresent()
        ) {
          throw new RuntimeException("Ya existe otro usuario con el mismo DNI");
        }

        // validar si se quiere cambiar el email a uno que ya existe en otro usuario
        if (
          userDTO.getEmail() != null &&
          !userDTO.getEmail().equals(existingUser.getEmail()) &&
          userRepository.findByEmail(userDTO.getEmail()).isPresent()
        ) {
          throw new RuntimeException(
            "Ya existe otro usuario con el mismo email"
          );
        }

        // evita sobreescribir campos críticos con valores nulos
        if (userDTO.getName() != null) existingUser.setName(userDTO.getName());
        if (userDTO.getEmail() != null) existingUser.setEmail(
          userDTO.getEmail()
        );
        if (userDTO.getDni() != null) existingUser.setDni(userDTO.getDni());
        if (userDTO.getRole() != null) existingUser.setRole(userDTO.getRole());
        if (userDTO.getStatus() != null) existingUser.setStatus(
          userDTO.getStatus()
        );

        if (userDTO.getPassword() != null && !userDTO.getPassword().isBlank()) {
          existingUser.setPassword(
            passwordEncoder.encode(userDTO.getPassword())
          );
        }

        User updatedUser = userRepository.save(existingUser);
        return modelMapper.map(updatedUser, UserDTO.class);
      })
      .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
  }

  public void deleteUser(Long id) {
    userRepository
      .findById(id)
      .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    userRepository.deleteById(id);
  }

  public void deactivateUser(Long id) {
    User user = userRepository
      .findById(id)
      .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    user.setStatus(Status.INACTIVE);
    userRepository.save(user);
  }
}
