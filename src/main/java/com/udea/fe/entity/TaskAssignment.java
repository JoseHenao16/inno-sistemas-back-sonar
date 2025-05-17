package com.udea.fe.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "task_assignment")
@Getter
@Setter

public class TaskAssignment {
    @EmbeddedId
    private TaskAssignmentId id;

    @ManyToOne
    @MapsId("taskId")
    @JoinColumn(name = "task_id")
    private Task task;

}
