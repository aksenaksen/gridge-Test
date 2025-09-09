package com.example.instagram.feed.application.dto.in;

public record FeedDeleteCommand(
        Long feedId,
        Long userId
) {
}
