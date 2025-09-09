package com.example.instagram.feed.application.dto.in;

public record UnLikeCommand(
        Long feedId,
        Long userId
) {
}
