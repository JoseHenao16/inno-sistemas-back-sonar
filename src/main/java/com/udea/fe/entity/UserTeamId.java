package com.udea.fe.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter

public class UserTeamId  implements Serializable {
    private Long userId;
    private Long teamId;
}
