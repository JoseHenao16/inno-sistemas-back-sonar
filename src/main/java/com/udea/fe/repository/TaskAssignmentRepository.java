package com.udea.fe.repository;

import com.udea.fe.entity.TaskAssignment;
import com.udea.fe.entity.TaskAssignmentId;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskAssignmentRepository extends JpaRepository<TaskAssignment, TaskAssignmentId> {
    List<TaskAssignment> findById_TaskId(Long taskId);
    List<TaskAssignment> findById_AssignedIdAndId_AssignedType(Long assignedId, String assignedType);
}

