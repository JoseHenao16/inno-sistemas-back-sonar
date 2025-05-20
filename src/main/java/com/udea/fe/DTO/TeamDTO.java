package com.udea.fe.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeamDTO {
    private Long id;
    private String name;
    private String description;
    private Long projectId;
    private Long leaderId;
}
