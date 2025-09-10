package com.example.instagram.subscription.infrastructor;

import com.example.instagram.subscription.domain.FollowStatus;
import com.example.instagram.subscription.domain.Subscription;

import java.time.LocalDateTime;
import java.util.List;

public interface SubscriptionRepositoryCustom {

    List<Subscription> findSubscriptionsByConditions(Long userId, FollowStatus status, LocalDateTime startAt, LocalDateTime endAt, Long limit);

    List<Subscription> findSubscriptionsByConditions(Long userId, FollowStatus status, LocalDateTime startAt, LocalDateTime endAt, Long limit, Long lastSubscriptionId);

    List<Long> findFollowingUserIds(Long followerId, FollowStatus status);
}