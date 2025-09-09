package com.example.instagram.comment.application.dto.out;

import com.example.instagram.comment.domain.Comment;
import com.example.instagram.comment.domain.CommentStatus;

import java.time.LocalDateTime;

public record CommentDto(
        Long commentId,
        String content,
        String path,
        Long feedId,
        Long writerId,
        CommentStatus status,
        LocalDateTime createdAt
) {
    public static CommentDto from(Comment comment) {
        return new CommentDto(
                comment.getCommentId(),
                comment.getContent(),
                comment.getCommentPath().getPath(),
                comment.getFeedId(),
                comment.getWriterId(),
                comment.getStatus(),
                comment.getCreatedAt()
        );
    }
}