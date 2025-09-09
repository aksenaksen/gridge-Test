package com.example.instagram.comment.application.dto.in;

public record CommentUpdateCommand(
        Long commentId,
        Long userId,
        String content
) {
}
