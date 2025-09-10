package com.example.instagram.feed_read.infrastructor;

import com.example.instagram.feed.application.dto.out.ResponseFeedDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FeedQueryModel {

    private Long feedId;
    private String content;
    private Long userId;
    private Long likeCount;
    private Long commentCount;
    private List<String> imageUrls;

    public static FeedQueryModel of(ResponseFeedDto feed, Long likeCount, Long commentCount){
        return new FeedQueryModel(
            feed.feedId(),
            feed.content(),
            feed.writerId(),
            likeCount,
            commentCount,
            feed.imageUrls()
        );
    }

    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {
        this.likeCount--;
    }

    public void incrementCommentCount() {
        this.commentCount++;
    }

    public void decrementCommentCount() {
        this.commentCount--;
    }

    public void updateContent(String newContent) {
        this.content = newContent;
    }
}
