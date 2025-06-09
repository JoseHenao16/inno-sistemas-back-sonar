package com.udea.fe.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskAssignmentRequestDTO {
    private Long taskId;
    private String assignedType;
    private Long assignedId;
}
