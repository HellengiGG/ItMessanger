package com.curse.ITteam_messenger.service.impl;

import com.curse.ITteam_messenger.dto.MessageDto;
import com.curse.ITteam_messenger.dto.MessageUpdateRequest;
import com.curse.ITteam_messenger.model.*;
import com.curse.ITteam_messenger.repository.*;
import com.curse.ITteam_messenger.dto.FileUploadResponse;
import com.curse.ITteam_messenger.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final FileAttachmentRepository fileRepo;
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    @Transactional
    public FileUploadResponse uploadFile(MultipartFile file, Long chatId, String username) throws IOException {
        if (file.isEmpty()) throw new RuntimeException("Пустой файл");

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));
        User sender = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!chat.getUsers().contains(sender)) throw new RuntimeException("Access denied");

        // 1. Сохраняем файл на диск
        Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(dir);
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path target = dir.resolve(filename);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        // 2. Сохраняем сообщение
        Message msg = new Message();
        msg.setContent(file.getOriginalFilename());
        msg.setSender(sender);
        msg.setChat(chat);
        messageRepository.save(msg); // ← теперь msg имеет ID

        // 3. Создаем и сохраняем attachment
        FileAttachment att = new FileAttachment();
        att.setFilename(file.getOriginalFilename());
        att.setUrl("/uploads/" + filename);
        att.setContentType(file.getContentType());
        att.setMessage(msg); // теперь msg с ID
        fileRepo.save(att);

        return new FileUploadResponse(msg.getId(), att.getUrl());
    }

    @Override
    @Transactional
    public MessageDto updateMessage(Long id, String username, MessageUpdateRequest req) {
        Message msg = messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        if (!msg.getSender().getUsername().equals(username)) {
            throw new RuntimeException("Access denied");
        }

        msg.setContent(req.getContent());
        msg.setUpdatedAt(LocalDateTime.now());
        return convertToDto(msg);
    }

    @Override
    @Transactional
    public void deleteMessage(Long id, String username) {
        Message msg = messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        if (!msg.getSender().getUsername().equals(username)) {
            throw new RuntimeException("Access denied");
        }

        messageRepository.delete(msg);
    }


    @Override
    @Transactional(readOnly = true)
    public List<MessageDto> getChatMessages(Long chatId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));

        if (!chat.getUsers().contains(user)) {
            throw new RuntimeException("User is not a member of this chat");
        }

        return messageRepository.findByChatIdOrderByCreatedAtAsc(chatId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MessageDto sendMessage(MessageDto messageDto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Chat chat = chatRepository.findById(messageDto.getChatId())
                .orElseThrow(() -> new RuntimeException("Chat not found"));

        if (!chat.getUsers().contains(user)) {
            throw new RuntimeException("User is not a member of this chat");
        }

        Message message = new Message();
        message.setContent(messageDto.getContent());
        message.setSender(user);
        message.setChat(chat);
        message.setUpdatedAt(message.getCreatedAt()); // при создании
        Message savedMessage = messageRepository.save(message);


        return convertToDto(savedMessage);
    }

    private MessageDto convertToDto(Message message) {
        MessageDto dto = new MessageDto();
        dto.setId(message.getId());
        dto.setContent(message.getContent());

        // ⬅️ 2. передаём ссылку, если вложение есть
        dto.setFileUrl(
                message.getAttachment() != null ? message.getAttachment().getUrl() : null
        );

        dto.setSenderUsername(message.getSender().getUsername());
        dto.setSenderRole(message.getSender().getTeamRole());
        dto.setChatId(message.getChat().getId());
        dto.setCreatedAt(message.getCreatedAt());
        dto.setUpdatedAt(message.getUpdatedAt());
        return dto;
    }



} 