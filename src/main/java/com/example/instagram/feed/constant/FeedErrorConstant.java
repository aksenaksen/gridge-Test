package com.example.instagram.feed.constant;

import com.example.instagram.common.exception.GlobalErrorConstant;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum FeedErrorConstant implements GlobalErrorConstant {

    NOT_ACTIVE_STATUS(FeedMessageConstant.NOT_ACTIVE_STATUS, HttpStatus.BAD_REQUEST),
    NOT_FOUND_FEED(FeedMessageConstant.NOT_FOUND_FEED, HttpStatus.NOT_FOUND),
    NOT_MATCHED_WRITER(FeedMessageConstant.NOT_MATCHED_WRITER, HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus httpStatus;

    FeedErrorConstant(String message, HttpStatus httpStatus){
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
