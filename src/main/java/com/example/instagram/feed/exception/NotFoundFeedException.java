package com.example.instagram.feed.exception;

import com.example.instagram.common.exception.GlobalException;
import com.example.instagram.feed.constant.FeedErrorConstant;

public class NotFoundFeedException extends GlobalException {

    public NotFoundFeedException() {
        super(FeedErrorConstant.NOT_FOUND_FEED);
    }
}
