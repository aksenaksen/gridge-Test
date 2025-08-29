package com.example.instagram.user.application.dto.in;

public record UserRegisterCommand(
        String username,
        String name,
        String password
) {
}
