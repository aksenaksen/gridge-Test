package com.example.instagram.feed.application.dto.out;

import com.example.instagram.feed.domain.Feed;
import com.example.instagram.feed.domain.FeedStatus;

import java.time.LocalDateTime;
import java.util.List;

public record ResponseFeedDetail(
        Long feedId,
        Long writerId,
        String content,
        FeedStatus status,
        List<String> imageUrls,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ResponseFeedDetail from(Feed feed) {
        return new ResponseFeedDetail(
                feed.getFeedId(),
                feed.getWriterId(),
                feed.getContent(),
                feed.getStatus(),
                feed.getImages().stream()
                        .map(feedImage -> feedImage.getImageUrl())
                        .toList(),
                feed.getCreatedAt(),
                feed.getUpdatedAt()
        );
    }
}