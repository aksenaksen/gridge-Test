package com.example.instagram.common.events;

public record UpdateFeedContentEvent(
        Long feedId,
        String content
) {
}
