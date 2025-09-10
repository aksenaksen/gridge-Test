package com.example.instagram.subscription.application.dto.in;

import com.example.instagram.subscription.domain.FollowStatus;

import java.time.LocalDate;

public record SubscriptionSearchCondition(
        Long userId,
        String name,
        FollowStatus status,
        LocalDate date
) {

}