package com.example.instagram.subscription.domain;

public enum FollowStatus {
    FOLLOWING,           // 현재 팔로잉 중
    UNFOLLOWED,       // 언팔로우
    BLOCKED_BY_FOLLOWEE   // 상대방이 나를 차단
}