package com.curse.ITteam_messenger.controller;

import com.curse.ITteam_messenger.dto.PrivateMessageDTO;
import com.curse.ITteam_messenger.service.PrivateMessageService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.util.List;

@RestController
@RequestMapping("/api/private-messages")
@RequiredArgsConstructor
public class PrivateMessageController {

    private final PrivateMessageService messageService;

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateMessage(@PathVariable Long id, @RequestBody String content, Authentication auth) {
        String username = auth.getName();
        messageService.updateMessage(id, content, username);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMessage(@PathVariable Long id, Authentication auth) {
        String username = auth.getName();
        messageService.deleteMessage(id, username);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<PrivateMessageDTO> send(@RequestBody PrivateMessageDTO dto, Authentication auth) {
        String sender = auth.getName();
        return ResponseEntity.ok(messageService.sendMessage(sender, dto));
    }

    @GetMapping("/{username}")
    public ResponseEntity<List<PrivateMessageDTO>> getWithUser(@PathVariable String username, Authentication auth) {
        String currentUser = auth.getName();
        return ResponseEntity.ok(messageService.getMessagesWith(currentUser, username));
    }
}
