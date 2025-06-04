package com.curse.ITteam_messenger.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}