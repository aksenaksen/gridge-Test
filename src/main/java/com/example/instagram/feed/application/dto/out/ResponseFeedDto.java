package com.example.instagram.feed.application.dto.out;

import com.example.instagram.feed.domain.Feed;
import com.example.instagram.feed.domain.FeedImage;
import com.example.instagram.feed.domain.FeedStatus;

import java.time.LocalDateTime;
import java.util.List;

public record ResponseFeedDto(
        Long feedId,
        Long writerId,
        String content,
        FeedStatus status,
        List<String> imageUrls,
        Integer imageCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ResponseFeedDto from(Feed feed) {
                
        return new ResponseFeedDto(
                feed.getFeedId(),
                feed.getWriterId(),
                feed.getContent(),
                feed.getStatus(),
                feed.getImages().stream().map(FeedImage::getImageUrl).toList(),
                feed.getImages().size(),
                feed.getCreatedAt(),
                feed.getUpdatedAt()
        );
    }
}