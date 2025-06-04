package com.curse.ITteam_messenger.dto;

import com.curse.ITteam_messenger.model.Message;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MessageDto {
    private Long id;
    private String content;

    private String fileUrl;          // ⬅️ 1. добавляем

    private String senderUsername;
    private String senderRole;
    private Long chatId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

