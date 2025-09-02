package com.example.instagram.user.presentation.in;

import com.example.instagram.user.domain.UserStatus;

import java.time.LocalDate;

public record RequestFindAllUserCondition(
        String username,
        String name,
        LocalDate date,
        UserStatus status
) {
}
