package com.example.instagram.feed.application.dto.out;

import com.example.instagram.feed.domain.LikeCount;

public record ResponseFeedLikeCount(
        Long feedId,
        Long likeCount
) {

    public static ResponseFeedLikeCount from(LikeCount likeCount){
        return new ResponseFeedLikeCount(likeCount.getFeedId(),likeCount.getLikeCount());
    }
}
