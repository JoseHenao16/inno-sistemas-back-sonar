package com.udea.fe.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskAssignmentResponseDTO {
    private Long taskId;
    private String assignedType;
    private Long assignedId;
    private String message;
}
