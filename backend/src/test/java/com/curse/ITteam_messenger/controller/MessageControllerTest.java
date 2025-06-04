package com.curse.ITteam_messenger.controller;

import com.curse.ITteam_messenger.dto.MessageRequest;
import com.curse.ITteam_messenger.dto.MessageResponse;
import com.curse.ITteam_messenger.model.Message;
import com.curse.ITteam_messenger.model.User;
import com.curse.ITteam_messenger.repository.MessageRepository;
import com.curse.ITteam_messenger.repository.UserRepository;
import com.curse.ITteam_messenger.security.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MessageRepository messageRepository;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private JwtProvider jwtProvider;

    private String jwtToken;
    private User testUser;
    private Message testMessage;

    @BeforeEach
    void setUp() {
        // Настройка контекста безопасности
        Authentication authentication = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
            "testuser", null, null);
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Настройка моков
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("password");

        testMessage = new Message();
        testMessage.setId(1L);
        testMessage.setContent("Test message");
        testMessage.setSender(testUser);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(messageRepository.save(any(Message.class))).thenReturn(testMessage);
        when(messageRepository.findAll()).thenReturn(Arrays.asList(testMessage));

        jwtToken = jwtProvider.generateToken(testUser.getUsername());
    }

    @Test
    @WithMockUser(username = "testuser")
    void createMessage_ValidRequest_ReturnsMessage() throws Exception {
        MessageRequest request = new MessageRequest();
        request.setContent("Test message");

        mockMvc.perform(post("/api/messages")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Test message"));
    }

    @Test
    void createMessage_Unauthenticated_ReturnsUnauthorized() throws Exception {
        MessageRequest request = new MessageRequest();
        request.setContent("Test message");

        mockMvc.perform(post("/api/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getAllMessages_ReturnsMessages() throws Exception {
        mockMvc.perform(get("/api/messages")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Test message"));
    }
} 