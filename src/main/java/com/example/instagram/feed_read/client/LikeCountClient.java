package com.example.instagram.feed_read.client;

import com.example.instagram.feed.application.like.FeedLikeService;
import com.example.instagram.feed_read.application.LikeClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeCountClient implements LikeClient {

    private final FeedLikeService feedLikeService;

    @Override
    public Long count(Long feedId) {
        return feedLikeService.findLikeCount(feedId).likeCount();
    };
}
