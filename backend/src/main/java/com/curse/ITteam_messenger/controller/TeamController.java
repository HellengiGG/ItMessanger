package com.curse.ITteam_messenger.controller;

import com.curse.ITteam_messenger.model.Team;
import com.curse.ITteam_messenger.model.User;
import com.curse.ITteam_messenger.repository.TeamRepository;
import com.curse.ITteam_messenger.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<Team>> getUserTeams() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Team> teams = List.of(user.getTeam());
        return ResponseEntity.ok(teams);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Team> getTeam(@PathVariable Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team not found"));
        return ResponseEntity.ok(team);
    }
} 