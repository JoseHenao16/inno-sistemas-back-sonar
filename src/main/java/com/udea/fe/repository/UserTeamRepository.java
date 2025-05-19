package com.udea.fe.repository;

import com.udea.fe.entity.Team;
import com.udea.fe.entity.UserTeam;
import com.udea.fe.entity.UserTeamId;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTeamRepository extends JpaRepository<UserTeam, UserTeamId> {
    List<UserTeam> findByIdUserId(Long userId);
    List<UserTeam> findByTeam(Team team);
}