package com.udea.fe.repository;

import com.udea.fe.entity.Role;
import com.udea.fe.entity.Status;
import com.udea.fe.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByDni(String dni);
    List<User> findByRole(Role role);
    List<User> findByStatus(Status status);
    List<User> findByRoleNot(Role role);
}
