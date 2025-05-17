package com.udea.fe.DTO;

import com.udea.fe.entity.ProjectStatus;

import java.time.LocalDate;

public class ProjectDTO {
    private Long id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private ProjectStatus status;
    private Long createdById;
}
