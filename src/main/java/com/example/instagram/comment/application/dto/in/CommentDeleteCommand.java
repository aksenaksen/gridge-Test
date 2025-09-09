package com.example.instagram.comment.application.dto.in;

public record CommentDeleteCommand(
        Long commentId,
        Long userId
) {
}