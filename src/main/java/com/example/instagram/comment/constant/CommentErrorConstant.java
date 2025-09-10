package com.example.instagram.comment.constant;

import com.example.instagram.common.exception.GlobalErrorConstant;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CommentErrorConstant implements GlobalErrorConstant {

    NOT_MATCHED_USER(CommentMessageConstant.NOT_MATCHED_USER, HttpStatus.BAD_REQUEST),
    OVER_COMMENT_LIMIT(CommentMessageConstant.OVER_COMMENT_LIMIT, HttpStatus.BAD_REQUEST),

    COMMENT_NOT_FOUND(CommentMessageConstant.COMMENT_NOT_FOUND, HttpStatus.NOT_FOUND),
    COMMENT_PARENT_NOT_FOUND(CommentMessageConstant.COMMENT_PARENT_NOT_FOUND, HttpStatus.NOT_FOUND),

    ALREADY_BLOCKED_OR_DELETED(CommentMessageConstant.ALREADY_BLOCKED_OR_DELETED, HttpStatus.BAD_REQUEST);


    private final String message;
    private final HttpStatus httpStatus;

    CommentErrorConstant(String message, HttpStatus httpStatus){
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
