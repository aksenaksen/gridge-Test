package com.example.instagram.subscription.application.dto.in;

public record SubscriptionBlockCommand (
        Long userId,
        Long followerId
){
}
