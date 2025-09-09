package com.example.instagram.feed.domain;

import com.example.instagram.common.RootBaseEntity;
import com.example.instagram.common.events.DeleteFeedEvent;
import com.example.instagram.common.events.ImageAddEvent;
import com.example.instagram.common.events.UpdateFeedContentEvent;
import com.example.instagram.feed.exception.FeedNotActiveException;
import com.example.instagram.feed.exception.NotMatchedWriterException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "feeds")
public class Feed extends RootBaseEntity<Feed> {
    @Id
    @Column(name = "feed_id")
    private Long feedId;

    private Long writerId;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private FeedStatus status;

    @OneToMany(mappedBy = "feed", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeedImage> images = new ArrayList<>();

    @Builder
    public Feed(Long writerId, String content, FeedStatus status) {
        this.writerId = writerId;
        this.content = content;
        this.status = status;
    }

    public static Feed createFeed(Long feedId, Long writerId, String content, FeedStatus status) {
        Feed feed = new Feed();

        feed.feedId = feedId;
        feed.writerId = writerId;
        feed.content = content;
        feed.status = status;

        return feed;
    }

    public void updateContent(String content) {
        this.content = content;
        this.registerEvent(new UpdateFeedContentEvent(this.feedId, content));
    }

    public void delete(Long userId) {
        validWriter(userId);
        this.status = FeedStatus.USER_DELETED;
        this.registerEvent(new DeleteFeedEvent(this.feedId));
    }

    public void deleteByAdmin(){
        this.status = FeedStatus.ADMIN_REMOVED;
        this.registerEvent(new DeleteFeedEvent(this.feedId));
    }

    public void addImage(FeedImage feedImage) {
        this.images.add(feedImage);
        feedImage.setFeed(this);
        this.registerEvent(new ImageAddEvent(this.feedId));
    }
    
    public void addImages(List<String> imageUrls) {
        if (imageUrls != null && !imageUrls.isEmpty()) {
            for (String imageUrl : imageUrls) {
                FeedImage feedImage = FeedImage.createImage(imageUrl);
                addImage(feedImage);
            }
        }
    }

    public void updateImages(List<String> imageUrls) {
        this.images.clear();
        addImages(imageUrls);
    }
    
    public void validateCanBeUpdated(Long userId) {
        validWriter(userId);

        if (this.status != FeedStatus.ACTIVE) {
            throw new FeedNotActiveException();
        }
    }

    public void validWriter(Long userId){
        if(!Objects.equals(userId, this.writerId)) throw new NotMatchedWriterException();
    }
}