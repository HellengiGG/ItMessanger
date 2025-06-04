package com.curse.ITteam_messenger.service;

import com.curse.ITteam_messenger.dto.ChatDto;

import java.util.List;

public interface ChatService {
    List<ChatDto> getUserChats(String username);
    ChatDto createChat(ChatDto chatDto, String username);
    ChatDto getChat(Long chatId, String username);


} 