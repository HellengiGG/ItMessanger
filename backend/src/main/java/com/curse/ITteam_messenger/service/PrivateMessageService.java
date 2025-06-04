package com.curse.ITteam_messenger.service;

import com.curse.ITteam_messenger.dto.FileUploadResponse;
import com.curse.ITteam_messenger.dto.PrivateMessageDTO;
import com.curse.ITteam_messenger.model.FileAttachment;
import com.curse.ITteam_messenger.model.PrivateMessage;
import com.curse.ITteam_messenger.model.User;
import com.curse.ITteam_messenger.repository.FileAttachmentRepository;
import com.curse.ITteam_messenger.repository.PrivateMessageRepository;
import com.curse.ITteam_messenger.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrivateMessageService {

    private final PrivateMessageRepository messageRepo;
    private final UserRepository userRepo;
    private final FileAttachmentRepository fileRepo;

    @Value("${file.upload-dir}")
    private String uploadDir;   // тот же каталог, что для групповых

    /* ---------- отправка простого текста ---------- */

    @Transactional
    public PrivateMessageDTO sendMessage(String senderUsername, PrivateMessageDTO dto) {
        User sender   = userRepo.findByUsername(senderUsername).orElseThrow();
        User receiver = userRepo.findByUsername(dto.getReceiverUsername()).orElseThrow();

        PrivateMessage msg = new PrivateMessage();
        msg.setSender(sender);
        msg.setReceiver(receiver);
        msg.setContent(dto.getContent());          // текст
        msg = messageRepo.save(msg);

        return toDto(msg);
    }

    /* ---------- обновление ---------- */

    @Transactional
    public void updateMessage(Long id, String content, String username) {
        PrivateMessage msg = messageRepo.findById(id).orElseThrow();
        if (!msg.getSender().getUsername().equals(username))
            throw new AccessDeniedException("Forbidden");
        msg.setContent(content);
        msg.setTimestamp(LocalDateTime.now());     // «updated»
    }

    /* ---------- удаление ---------- */

    @Transactional
    public void deleteMessage(Long id, String username) {
        PrivateMessage msg = messageRepo.findById(id).orElseThrow();
        if (!msg.getSender().getUsername().equals(username))
            throw new AccessDeniedException("Forbidden");
        messageRepo.delete(msg);
    }

    /* ---------- отправка файла ---------- */



    @Transactional(readOnly = true)
    public List<PrivateMessageDTO> getMessagesWith(String me, String companion) {
        User u1 = userRepo.findByUsername(me).orElseThrow();
        User u2 = userRepo.findByUsername(companion).orElseThrow();

        return messageRepo
                .findBySenderAndReceiverOrReceiverAndSender(u1, u2, u1, u2)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /* ---------- util ---------- */

    private PrivateMessageDTO toDto(PrivateMessage m) {
        PrivateMessageDTO dto = new PrivateMessageDTO();
        dto.setId(m.getId());
        dto.setSenderUsername(m.getSender().getUsername());
        dto.setReceiverUsername(m.getReceiver().getUsername());
        dto.setContent(m.getContent());
        dto.setTimestamp(m.getTimestamp().toString());

        return dto;
    }
}
