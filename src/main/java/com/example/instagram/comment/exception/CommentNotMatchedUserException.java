package com.example.instagram.comment.exception;

import com.example.instagram.comment.constant.CommentErrorConstant;
import com.example.instagram.common.exception.GlobalException;

public class CommentNotMatchedUserException extends GlobalException {

    public CommentNotMatchedUserException(){
        super(CommentErrorConstant.NOT_MATCHED_USER);
    }
}
