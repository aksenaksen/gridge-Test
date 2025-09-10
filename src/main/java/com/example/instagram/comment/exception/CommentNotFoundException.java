package com.example.instagram.comment.exception;

import com.example.instagram.comment.constant.CommentErrorConstant;
import com.example.instagram.common.exception.GlobalException;

public class CommentNotFoundException extends GlobalException {

    public CommentNotFoundException() {
        super(CommentErrorConstant.COMMENT_NOT_FOUND);
    }
}
