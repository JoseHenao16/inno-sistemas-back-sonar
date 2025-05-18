package com.udea.fe.security.service;

import com.udea.fe.entity.User;
import com.udea.fe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getStatus().equals(com.udea.fe.entity.Status.ACTIVE),
                true,   // accountNonExpired
                true,   // credentialsNonExpired
                true,   // accountNonLocked
                user.getRole().getAuthorities()
        );
    }
}
