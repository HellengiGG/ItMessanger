package com.curse.ITteam_messenger.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MessageResponse {
    private Long id;
    private String content;
    private String username;
    private String senderRole;
    private LocalDateTime createdAt;
} 