package com.udea.fe.repository;

import com.udea.fe.entity.Team;
import com.udea.fe.entity.UserTeam;
import com.udea.fe.entity.UserTeamId;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserTeamRepository extends JpaRepository<UserTeam, UserTeamId> {
    List<UserTeam> findByIdUserId(Long userId);
    List<UserTeam> findByTeam(Team team);
    @Query("SELECT COUNT(ut) > 0 FROM UserTeam ut WHERE ut.user.userId = :userId AND ut.team.project.projectId = :projectId")
    boolean existsByUserIdAndProjectId(Long userId, Long projectId);
}