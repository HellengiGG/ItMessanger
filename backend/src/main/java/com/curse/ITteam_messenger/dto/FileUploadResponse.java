package com.curse.ITteam_messenger.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class FileUploadResponse {
    private Long messageId;
    private String fileUrl;
}
