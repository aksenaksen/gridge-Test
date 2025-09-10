package com.example.instagram.comment.presentation.in;

import com.example.instagram.comment.constant.CommentMessageConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RequestUpdateComment(

        @NotNull(message = CommentMessageConstant.COMMENT_ID_NOT_NULL)
        Long commentId,
        @NotBlank(message = CommentMessageConstant.COMMENT_NOT_NULL)
        @Size(max = 100, message = CommentMessageConstant.COMMENT_OVER_SIZE)
        String content
) {
}
