package com.curse.ITteam_messenger.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String teamRole; // DEVELOPER, PM, ANALYST, QA, DEVOPS, DESIGNER
    private String experienceLevel; // JUNIOR, MIDDLE, SENIOR
    private String teamName;
}