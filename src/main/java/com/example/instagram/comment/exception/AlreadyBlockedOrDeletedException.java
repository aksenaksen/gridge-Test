package com.example.instagram.comment.exception;

import com.example.instagram.comment.constant.CommentErrorConstant;
import com.example.instagram.common.exception.GlobalException;

public class AlreadyBlockedOrDeletedException extends GlobalException {

    public AlreadyBlockedOrDeletedException() {
        super(CommentErrorConstant.ALREADY_BLOCKED_OR_DELETED);
    }
}
