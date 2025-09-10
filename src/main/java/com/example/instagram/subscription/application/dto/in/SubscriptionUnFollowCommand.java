package com.example.instagram.subscription.application.dto.in;

public record SubscriptionUnFollowCommand(
        Long followeeId,
        Long followerId
) {
}
