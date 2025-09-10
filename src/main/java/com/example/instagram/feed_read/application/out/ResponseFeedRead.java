package com.example.instagram.feed_read.application.out;

import com.example.instagram.feed_read.infrastructor.FeedQueryModel;

import java.util.List;

public record ResponseFeedRead(
        Long feedId,
        String content,
        Long userId,
        Long likeCount,
        Long commentCount,
        List<String> imageUrls
) {
    public static ResponseFeedRead from(FeedQueryModel model) {
        return new ResponseFeedRead(
                model.getFeedId(),
                model.getContent(),
                model.getUserId(),
                model.getLikeCount(),
                model.getCommentCount(),
                model.getImageUrls()
        );
    }

    public static List<ResponseFeedRead> from(List<FeedQueryModel> models) {
        return models.stream()
                .map(ResponseFeedRead::from)
                .toList();
    }
}