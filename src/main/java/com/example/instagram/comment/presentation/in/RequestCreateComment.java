package com.example.instagram.comment.presentation.in;

import com.example.instagram.comment.application.dto.in.CommentCreateCommand;
import com.example.instagram.comment.constant.CommentMessageConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RequestCreateComment(

        @NotBlank(message = CommentMessageConstant.COMMENT_NOT_NULL)
        @Size(max = 100, message = CommentMessageConstant.COMMENT_OVER_SIZE)
        String content,
        
        @NotNull(message = CommentMessageConstant.FEED_NOT_NULL)
        Long feedId,
        
        String parentPath
) {
    public CommentCreateCommand toCommand(Long writerId) {
        return new CommentCreateCommand(content, feedId, writerId, parentPath);
    }
}