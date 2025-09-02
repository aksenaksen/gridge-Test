package com.example.instagram.user.presentation.out;

import com.example.instagram.user.application.dto.out.UserDto;
import com.example.instagram.user.domain.UserStatus;

import java.time.LocalDateTime;

public record ResponseUser(
        Long userId,
        String username,
        String name,
        UserStatus status,
        LocalDateTime lastLoginAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ResponseUser from(UserDto user) {
        return new ResponseUser(
                user.userId(),
                user.username(),
                user.name(),
                user.status(),
                user.lastLoginAt(),
                user.createdAt(),
                user.updatedAt()
        );
    }
}
