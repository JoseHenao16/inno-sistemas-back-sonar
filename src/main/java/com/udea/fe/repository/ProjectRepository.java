package com.udea.fe.repository;

import com.udea.fe.entity.Project;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByCreatedByUserId(Long userId);

}
