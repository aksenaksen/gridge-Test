package com.example.instagram.subscription.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FollowRelation {

    private Long followerId;
    private Long followeeId;

    private FollowRelation(Long followerId, Long followeeId) {
        this.followerId = followerId;
        this.followeeId = followeeId;
    }

    public static FollowRelation createRelation(Long followerId, Long followeeId) {
        return new FollowRelation(followerId, followeeId);
    }

    public Long getFollowerId() {
        return followerId;
    }

    public Long getFolloweeId() {
        return followeeId;
    }
}
