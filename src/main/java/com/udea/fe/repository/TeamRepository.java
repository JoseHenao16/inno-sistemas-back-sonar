package com.udea.fe.repository;

import com.udea.fe.entity.Project;
import com.udea.fe.entity.Team;
import com.udea.fe.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByProject(Project project);
    List<Team> findByLeader(User leader);
    Optional<Team> findByNameAndProject(String name, Project project);
}

