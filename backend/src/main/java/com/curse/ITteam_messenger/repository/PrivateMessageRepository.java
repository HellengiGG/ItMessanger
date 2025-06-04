package com.curse.ITteam_messenger.repository;


import com.curse.ITteam_messenger.model.PrivateMessage;
import com.curse.ITteam_messenger.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PrivateMessageRepository extends JpaRepository<PrivateMessage, Long> {
    List<PrivateMessage> findBySenderOrReceiver(User sender, User receiver);
    List<PrivateMessage> findBySenderAndReceiverOrReceiverAndSender(User s1, User r1, User s2, User r2);
}
