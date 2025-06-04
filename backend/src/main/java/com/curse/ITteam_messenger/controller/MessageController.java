package com.curse.ITteam_messenger.controller;

import com.curse.ITteam_messenger.dto.FileUploadResponse;
import com.curse.ITteam_messenger.dto.MessageDto;
import com.curse.ITteam_messenger.dto.MessageUpdateRequest;
import com.curse.ITteam_messenger.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("chatId") Long chatId,
            Authentication auth) throws IOException {
        String username = auth.getName(); // ⬅️ НЕ null?
        return ResponseEntity.ok(messageService.uploadFile(file, chatId, username));
    }




    @PatchMapping("/{id}")
    public ResponseEntity<MessageDto> editMessage(
            @PathVariable Long id,
            @RequestBody MessageUpdateRequest req,
            Authentication auth) {
        String username = auth.getName();
        return ResponseEntity.ok(messageService.updateMessage(id, username, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable Long id,
            Authentication auth) {
        String username = auth.getName();
        messageService.deleteMessage(id, username);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/chat/{chatId}")
    public ResponseEntity<List<MessageDto>> getChatMessages(@PathVariable Long chatId, Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(messageService.getChatMessages(chatId, username));
    }

    @PostMapping
    public ResponseEntity<MessageDto> sendMessage(@RequestBody MessageDto messageDto, Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(messageService.sendMessage(messageDto, username));
    }
}

