package com.example.instagram.feed.exception;

import com.example.instagram.common.exception.GlobalException;
import com.example.instagram.feed.constant.FeedErrorConstant;

public class FeedNotActiveException extends GlobalException {

    public FeedNotActiveException() {
        super(FeedErrorConstant.NOT_ACTIVE_STATUS);
    }
}
