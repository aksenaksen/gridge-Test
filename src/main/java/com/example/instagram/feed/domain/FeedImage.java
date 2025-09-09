package com.example.instagram.feed.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "feed_images")
public class FeedImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long imageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id", nullable = false)
    private Feed feed;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    public static FeedImage createImage(String imageUrl) {
        FeedImage feedImage = new FeedImage();
        feedImage.imageUrl = imageUrl;
        return feedImage;
    }

    public void setFeed(Feed feed) {
        this.feed = feed;
    }

}