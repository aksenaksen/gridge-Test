package com.example.instagram.feed.exception;

import com.example.instagram.common.exception.GlobalException;
import com.example.instagram.feed.constant.FeedErrorConstant;

public class NotMatchedWriterException extends GlobalException {

    public NotMatchedWriterException() {
        super(FeedErrorConstant.NOT_MATCHED_WRITER);
    }
}
