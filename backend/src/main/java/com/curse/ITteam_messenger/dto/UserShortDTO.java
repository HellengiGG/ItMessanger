package com.curse.ITteam_messenger.dto;

import lombok.Data;

@Data
public class UserShortDTO {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String teamRole;
    private String experienceLevel;
}
