package com.udea.fe.mapper;

import com.udea.fe.entity.Project;
import com.udea.fe.DTO.ProjectDTO;
import org.mapstruct.Mapper;

import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    @Mapping(target = "id", source = "projectId")
    @Mapping(target = "createdById", source = "createdBy.userId")
    ProjectDTO toDTO(Project project);
}

