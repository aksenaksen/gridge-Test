package com.example.instagram.feed.application.dto.in;

import java.util.List;

public record FeedUpdateCommand(
        Long feedId,
        Long userId,
        String content,
        List<String> imageUrls
) {
}