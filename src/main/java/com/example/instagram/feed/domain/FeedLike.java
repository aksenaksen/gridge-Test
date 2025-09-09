package com.example.instagram.feed.domain;

import com.example.instagram.common.RootBaseEntity;
import com.example.instagram.common.events.LikeEvent;
import com.example.instagram.common.events.UnLikeEvent;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "feed_like")
public class FeedLike extends RootBaseEntity<FeedLike> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_like_id")
    private Long feedLikeId;
    
    @Column(name = "feed_id", nullable = false)
    private Long feedId;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;

    public static FeedLike create(Long feedId, Long userId) {
        FeedLike feedLike = new FeedLike();
        feedLike.feedId = feedId;
        feedLike.userId = userId;
        return feedLike.andEvent(new LikeEvent(feedId));
    }

    public void publishUnlikeEvent(){
        this.registerEvent(new UnLikeEvent(feedId));
    }
}
