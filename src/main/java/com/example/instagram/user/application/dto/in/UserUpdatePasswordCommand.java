package com.example.instagram.user.application.dto.in;

public record UserUpdatePasswordCommand(
        String username,
        String phoneNumber,
        String password
) {
}
