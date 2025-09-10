package com.example.instagram.subscription.application.dto.in;

public record SubscriptionFollowCommand(
        Long followeeId,
        Long followerId
) {
}
