package com.example.instagram.common.events;

public record DeleteCommentEvent(
        Long commentId,
        Long feedId
) {
}
