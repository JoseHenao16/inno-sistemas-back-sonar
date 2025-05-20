package com.udea.fe.mapper;

import com.udea.fe.entity.Task;
import com.udea.fe.DTO.TaskDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(target = "id", source = "taskId")
    @Mapping(target = "projectId", source = "project.projectId")
    @Mapping(target = "createdById", source = "createdBy.userId")
    TaskDTO toDTO(Task task);
}
