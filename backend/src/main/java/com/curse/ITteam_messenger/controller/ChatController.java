package com.curse.ITteam_messenger.controller;

import com.curse.ITteam_messenger.dto.ChatDto;
import com.curse.ITteam_messenger.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;




    @GetMapping
    public ResponseEntity<List<ChatDto>> getUserChats(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(chatService.getUserChats(username));
    }

    @PostMapping
    public ResponseEntity<ChatDto> createChat(@RequestBody ChatDto chatDto, Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(chatService.createChat(chatDto, username));
    }

    @GetMapping("/{chatId}")
    public ResponseEntity<ChatDto> getChat(@PathVariable Long chatId, Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(chatService.getChat(chatId, username));
    }
} 