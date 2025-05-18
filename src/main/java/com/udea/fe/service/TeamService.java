package com.udea.fe.service;

import com.udea.fe.DTO.TeamDTO;
import com.udea.fe.entity.Project;
import com.udea.fe.entity.Team;
import com.udea.fe.entity.User;
import com.udea.fe.repository.ProjectRepository;
import com.udea.fe.repository.TeamRepository;
import com.udea.fe.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public TeamDTO createTeam(TeamDTO teamDTO) {
        Team team = modelMapper.map(teamDTO, Team.class);

        Project project = projectRepository.findById(teamDTO.getProjectId())
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
        team.setProject(project);

        if (teamDTO.getLeaderId() != null) {
            User leader = userRepository.findById(teamDTO.getLeaderId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            team.setLeader(leader);
        }

        Team savedTeam = teamRepository.save(team);
        return modelMapper.map(savedTeam, TeamDTO.class);
    }

    public TeamDTO getTeamById(Long id) {
        return teamRepository.findById(id)
                .map(team -> modelMapper.map(team, TeamDTO.class))
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));
    }

    public List<TeamDTO> getTeamsByProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        return teamRepository.findByProject(project).stream()
                .map(team -> modelMapper.map(team, TeamDTO.class))
                .collect(Collectors.toList());
    }


}
