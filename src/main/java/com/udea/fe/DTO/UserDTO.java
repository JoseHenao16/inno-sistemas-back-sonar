package com.udea.fe.DTO;

import com.udea.fe.entity.Role;
import com.udea.fe.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String dni;
    private Role role;
    private Status status;
}
