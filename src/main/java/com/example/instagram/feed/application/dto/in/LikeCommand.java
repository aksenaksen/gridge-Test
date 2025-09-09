package com.example.instagram.feed.application.dto.in;

public record LikeCommand(
        Long feedId,
        Long userId
) {
}
