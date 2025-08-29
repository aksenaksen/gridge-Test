package com.example.instagram.user.application.dto.out;

import com.example.instagram.user.domain.User;
import com.example.instagram.user.domain.UserStatus;

import java.time.LocalDateTime;

public record UserDto(
        Long userId,
        String username,
        String name,
        UserStatus status,
        LocalDateTime lastLoginAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static UserDto from(User user) {
        return new UserDto(
                user.getUserId(),
                user.getUsername(),
                user.getName(),
                user.getStatus(),
                user.getLastLoginAt(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
