package com.curse.ITteam_messenger.controller;

import com.curse.ITteam_messenger.dto.LoginRequest;
import com.curse.ITteam_messenger.dto.RegisterRequest;
import com.curse.ITteam_messenger.model.User;
import com.curse.ITteam_messenger.repository.UserRepository;
import com.curse.ITteam_messenger.security.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(jwtProvider.generateToken(any(Authentication.class))).thenReturn("test-token");
    }

    @Test
    void register_ValidRequest_ReturnsSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("password123");
        request.setEmail("test@example.com");
        request.setFirstName("Test");
        request.setLastName("User");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));
    }

    @Test
    void register_ExistingUsername_ReturnsBadRequest() throws Exception {
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        RegisterRequest request = new RegisterRequest();
        request.setUsername("existinguser");
        request.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Username is already taken"));
    }

    @Test
    void login_ValidCredentials_ReturnsToken() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        Authentication authentication = new UsernamePasswordAuthenticationToken(
            request.getUsername(), request.getPassword());
        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-token"))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void login_InvalidCredentials_ReturnsUnauthorized() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("wrongpassword");

        when(authenticationManager.authenticate(any()))
            .thenThrow(new BadCredentialsException("Invalid credentials"));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
} 