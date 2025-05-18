package com.udea.fe.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TeamDTO {
    private Long id;
    private String name;
    private String description;
    private Long projectId;
    private Long leaderId;
}
