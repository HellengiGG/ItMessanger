package com.curse.ITteam_messenger.repository;

import com.curse.ITteam_messenger.model.Chat;
import com.curse.ITteam_messenger.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findByUsersContaining(User user);
    List<Chat> findByName(String name);
} 