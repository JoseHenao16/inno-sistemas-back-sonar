package com.udea.fe.entity;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
public class TaskAssignmentId implements Serializable {
    private Long taskId;
    private String assignedType;
    private Long assignedId;
}
