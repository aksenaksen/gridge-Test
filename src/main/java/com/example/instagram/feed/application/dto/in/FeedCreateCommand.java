package com.example.instagram.feed.application.dto.in;

import java.util.List;

public record FeedCreateCommand(
        Long writerId,
        String content,
        List<String> imageUrls
) {
}