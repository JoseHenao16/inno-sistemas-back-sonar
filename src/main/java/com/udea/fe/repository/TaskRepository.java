package com.udea.fe.repository;

import com.udea.fe.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
    boolean existsByNameAndProject_ProjectId(String name, Long projectId);
}
