package com.example.instagram.feed.domain;

import com.example.instagram.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "like_count")
public class LikeCount extends BaseEntity {

    @Id
    @Column(name = "feed_id")
    private Long feedId;

    @Column(name = "like_count", nullable = false)
    private Long likeCount;

    public static LikeCount create(Long feedId, Long likeCount){
        LikeCount likeCountEntity = new LikeCount();
        likeCountEntity.feedId = feedId;
        likeCountEntity.likeCount = likeCount;
        return likeCountEntity;
    }

    public void incrementCount() {
        this.likeCount++;
    }

    public void decrementCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }
}
