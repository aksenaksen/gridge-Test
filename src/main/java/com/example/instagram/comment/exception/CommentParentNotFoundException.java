package com.example.instagram.comment.exception;

import com.example.instagram.comment.constant.CommentErrorConstant;
import com.example.instagram.common.exception.GlobalException;

public class CommentParentNotFoundException extends GlobalException {
    public CommentParentNotFoundException() {
        super(CommentErrorConstant.COMMENT_PARENT_NOT_FOUND);
    }
}
