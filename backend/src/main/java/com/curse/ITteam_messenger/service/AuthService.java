package com.curse.ITteam_messenger.service;

import com.curse.ITteam_messenger.dto.LoginRequest;
import com.curse.ITteam_messenger.dto.RegisterRequest;
import com.curse.ITteam_messenger.dto.ChatDto;
import com.curse.ITteam_messenger.model.Chat;
import com.curse.ITteam_messenger.model.Team;
import com.curse.ITteam_messenger.model.User;
import com.curse.ITteam_messenger.repository.TeamRepository;
import com.curse.ITteam_messenger.repository.UserRepository;
import com.curse.ITteam_messenger.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.curse.ITteam_messenger.repository.ChatRepository;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final ChatService chatService;
    private final ChatRepository chatRepository;


    public String register(RegisterRequest request) {
        // Проверяем существование команды
        Team team = teamRepository.findByName(request.getTeamName())
                .orElseGet(() -> {
                    Team newTeam = new Team();
                    newTeam.setName(request.getTeamName());
                    newTeam.setDescription(request.getTeamName());
                    return teamRepository.save(newTeam);
                });

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole("USER");
        user.setTeamRole(request.getTeamRole());
        user.setExperienceLevel(request.getExperienceLevel());
        user.setTeam(team);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        // Create or get team chat
        ChatDto teamChatDto = new ChatDto();
        teamChatDto.setName(request.getTeamName());
        chatService.createChat(teamChatDto, request.getUsername());

        return jwtProvider.generateToken(user.getUsername());
    }

    public void joinOrCreateTeamChat(User user) {
        if (user.getTeam() == null) return;

        String teamName = user.getTeam().getName();

        List<Chat> existingChats = chatRepository.findByName(teamName);
        Chat chat;
        if (existingChats.isEmpty()) {
            chat = new Chat();
            chat.setName(teamName);
            chat.setCreator(user);
            chat.setUsers(new ArrayList<>());
        } else {
            chat = existingChats.get(0);
        }

        if (!chat.getUsers().contains(user)) {
            chat.getUsers().add(user);
            chatRepository.save(chat);
        }
    }


    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }


    public String login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtProvider.generateToken(request.getUsername());
    }
}
