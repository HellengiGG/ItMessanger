package com.curse.ITteam_messenger.controller;

import com.curse.ITteam_messenger.dto.LoginRequest;
import com.curse.ITteam_messenger.dto.RegisterRequest;
import com.curse.ITteam_messenger.model.User;
import com.curse.ITteam_messenger.security.JwtProvider;
import com.curse.ITteam_messenger.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AuthService authService;
    private final JwtProvider jwtProvider;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        log.info("Received registration request: {}", request);

        // Validation (можно выносить отдельно)
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Username is required");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Password is required");
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required");
        }
        if (request.getTeamName() == null || request.getTeamName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Team name is required");
        }
        if (request.getTeamRole() == null || request.getTeamRole().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Team role is required");
        }
        if (request.getExperienceLevel() == null || request.getExperienceLevel().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Experience level is required");
        }

        try {
            String token = authService.register(request);
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("username", request.getUsername());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error during registration: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        if (request.getUsername() == null || request.getPassword() == null) {
            return ResponseEntity.badRequest().body("Username and password are required");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtProvider.generateToken(authentication);

            // 🔥 Получаем пользователя и добавляем в командный чат
            User user = authService.getUserByUsername(request.getUsername());
            authService.joinOrCreateTeamChat(user);

            Map<String, String> response = new HashMap<>();
            response.put("token", jwt);
            response.put("username", request.getUsername());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid username or password");
        }
    }
}
