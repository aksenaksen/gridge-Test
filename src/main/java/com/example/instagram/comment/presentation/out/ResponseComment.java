package com.example.instagram.comment.presentation.out;

import com.example.instagram.comment.application.dto.out.CommentDto;
import com.example.instagram.comment.domain.CommentStatus;

import java.time.LocalDateTime;

public record ResponseComment(
        Long commentId,
        String content,
        String path,
        Long feedId,
        Long writerId,
        CommentStatus status,
        LocalDateTime createdAt
) {
    public static  ResponseComment from(CommentDto comment) {
        return new ResponseComment(
                comment.commentId(),
                comment.content(),
                comment.path(),
                comment.feedId(),
                comment.writerId(),
                comment.status(),
                comment.createdAt()
        );
    }
}
