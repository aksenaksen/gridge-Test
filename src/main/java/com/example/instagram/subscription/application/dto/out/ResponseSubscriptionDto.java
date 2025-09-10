package com.example.instagram.subscription.application.dto.out;

import com.example.instagram.subscription.domain.FollowStatus;
import com.example.instagram.subscription.domain.Subscription;

import java.time.LocalDateTime;

public record ResponseSubscriptionDto(
        Long subscriptionId,
        Long followerId,
        Long followeeId,
        FollowStatus followStatus,
        LocalDateTime startAt,
        LocalDateTime endAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ResponseSubscriptionDto from(Subscription subscription) {
        return new ResponseSubscriptionDto(
                subscription.getSubscriptionId(),
                subscription.getFollowRelation().getFollowerId(),
                subscription.getFollowRelation().getFolloweeId(),
                subscription.getFollowStatus(),
                subscription.getStartAt(),
                subscription.getEndAt(),
                subscription.getCreatedAt(),
                subscription.getUpdatedAt()
        );
    }
}