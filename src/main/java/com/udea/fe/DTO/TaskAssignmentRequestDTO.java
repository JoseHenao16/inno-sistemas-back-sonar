package com.udea.fe.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskAssignmentRequestDTO {
    private Long taskId;
    private String assignedType;
    private Long assignedId;
}
