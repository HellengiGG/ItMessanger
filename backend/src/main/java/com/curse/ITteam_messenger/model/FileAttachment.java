package com.curse.ITteam_messenger.model;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "file_attachments")
public class FileAttachment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;          // IMG_123.png
    private String url;               // /uploads/IMG_123.png
    private String contentType;       // image/png, application/pdf

    @OneToOne(fetch = FetchType.LAZY) @JoinColumn(name = "message_id")
    private Message message;
}
