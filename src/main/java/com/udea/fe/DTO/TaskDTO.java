package com.udea.fe.DTO;

import com.udea.fe.entity.TaskPriority;
import com.udea.fe.entity.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime dueDate;
    private TaskStatus status;
    private TaskPriority priority;
    private Long projectId;
    private Long createdById;
}
