package com.example.instagram.comment.exception;

import com.example.instagram.comment.constant.CommentErrorConstant;
import com.example.instagram.common.exception.GlobalException;

public class OverCommentLimitException extends GlobalException {

    public OverCommentLimitException() {
        super(CommentErrorConstant.OVER_COMMENT_LIMIT);
    }
}
