package com.utcn.edu_digital.auth;

import lombok.Data;

@Data
public class LoginRequest {
    private String login;
    private String password;
}
