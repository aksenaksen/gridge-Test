package com.example.instagram.user.application.dto.out;

import com.example.instagram.user.domain.User;
import com.example.instagram.user.domain.UserStatus;

import java.time.LocalDateTime;

public record ResponseUserDto(
        Long userId,
        String username,
        String name,
        UserStatus status,
        LocalDateTime lastLoginAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ResponseUserDto from(User user) {
        return new ResponseUserDto(
                user.getUserId(),
                user.getUsername(),
                user.getProfile().getName(),
                user.getStatus(),
                user.getLastLoginAt(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
