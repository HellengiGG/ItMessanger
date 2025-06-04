package com.curse.ITteam_messenger.dto;

import lombok.Data;

@Data
public class PrivateMessageDTO {
    private Long id;
    private String senderUsername;
    private String receiverUsername;
    private String content;
    private String timestamp;
    private String fileUrl;
}

