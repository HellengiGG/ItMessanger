package com.curse.ITteam_messenger.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatDto {
    private Long id;
    private String name;
    private Long creatorId;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
} 