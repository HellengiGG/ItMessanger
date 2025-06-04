package com.curse.ITteam_messenger.model;

import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
@Data
@Entity
@Table(name = "messages")
@EqualsAndHashCode(of = "id")
public class Message {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)  @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)  @JoinColumn(name = "chat_id")
    private Chat chat;

    @Column(nullable = false)          private LocalDateTime createdAt = LocalDateTime.now();
    @Column(name = "updated_at")       private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private FileAttachment attachment;
}
