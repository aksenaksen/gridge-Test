package com.example.instagram.comment.application.dto.in;

public record CommentCreateCommand(
        String content,
        Long feedId,
        Long writerId,
        String parentPath
) {
}