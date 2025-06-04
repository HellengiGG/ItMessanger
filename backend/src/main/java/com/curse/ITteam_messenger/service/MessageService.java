package com.curse.ITteam_messenger.service;

import com.curse.ITteam_messenger.dto.FileUploadResponse;
import com.curse.ITteam_messenger.dto.MessageDto;
import com.curse.ITteam_messenger.dto.MessageUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MessageService {
    List<MessageDto> getChatMessages(Long chatId, String username);
    MessageDto sendMessage(MessageDto messageDto, String username);
    MessageDto updateMessage(Long id, String username, MessageUpdateRequest req);
    void deleteMessage(Long id, String username);
    FileUploadResponse uploadFile(MultipartFile file, Long chatId, String username) throws IOException;

}
