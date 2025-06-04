package com.curse.ITteam_messenger.service.impl;

import com.curse.ITteam_messenger.dto.ChatDto;
import com.curse.ITteam_messenger.model.Chat;
import com.curse.ITteam_messenger.model.User;
import com.curse.ITteam_messenger.repository.ChatRepository;
import com.curse.ITteam_messenger.repository.UserRepository;
import com.curse.ITteam_messenger.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ChatDto> getUserChats(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String teamName = user.getTeam() != null ? user.getTeam().getName() : null;

        return chatRepository.findByUsersContaining(user).stream()
                .sorted((c1, c2) -> {
                    if (teamName == null) return 0;
                    if (c1.getName().equals(teamName)) return -1;
                    if (c2.getName().equals(teamName)) return 1;
                    return 0;
                })
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public ChatDto createChat(ChatDto chatDto, String username) {
        User creator = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if this is a team chat and if it already exists

         // Remove "Team " prefix
            List<Chat> existingTeamChats = chatRepository.findByName(chatDto.getName());
            
            if (!existingTeamChats.isEmpty()) {
                // Team chat exists, add user to it
                Chat existingChat = existingTeamChats.get(0);
                if (!existingChat.getUsers().contains(creator)) {
                    existingChat.getUsers().add(creator);
                    chatRepository.save(existingChat);
                }
                return convertToDto(existingChat);
            }


        // Create new chat
        Chat chat = new Chat();
        chat.setName(chatDto.getName());
        chat.setCreator(creator);
        chat.getUsers().add(creator);

        Chat savedChat = chatRepository.save(chat);
        return convertToDto(savedChat);
    }

    @Override
    @Transactional(readOnly = true)
    public ChatDto getChat(Long chatId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));

        if (!chat.getUsers().contains(user)) {
            throw new RuntimeException("User is not a member of this chat");
        }

        return convertToDto(chat);
    }

    private ChatDto convertToDto(Chat chat) {
        ChatDto dto = new ChatDto();
        dto.setId(chat.getId());
        dto.setName(chat.getName());
        dto.setCreatorId(chat.getCreator().getId());
        
        if (!chat.getMessages().isEmpty()) {
            var lastMessage = chat.getMessages().get(chat.getMessages().size() - 1);
            dto.setLastMessage(lastMessage.getContent());
            dto.setLastMessageTime(lastMessage.getCreatedAt());
        }
        
        return dto;
    }
} 