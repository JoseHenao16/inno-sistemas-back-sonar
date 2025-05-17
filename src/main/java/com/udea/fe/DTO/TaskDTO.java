package com.udea.fe.DTO;

import com.udea.fe.entity.TaskPriority;
import com.udea.fe.entity.TaskStatus;

import java.time.LocalDateTime;

public class TaskDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private TaskStatus status;
    private TaskPriority priority;
    private Long projectId;
    private Long createdById;
}
