package com.example.instagram.subscription.domain;

import com.example.instagram.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "subscriptions")
public class Subscription extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscription_id")
    private Long subscriptionId;

    @Embedded
    private FollowRelation followRelation;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at")
    private LocalDateTime endAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "follow_status", nullable = false)
    private FollowStatus followStatus;

    public static Subscription createSubscription(Long followerId, Long followId){

        Subscription subscription = new Subscription();
        subscription.followRelation = FollowRelation.createRelation(followerId, followId);

        subscription.followStatus = FollowStatus.FOLLOWING;
        subscription.startAt = LocalDateTime.now();

        return subscription;
    }

    public void canFollow(){
        if(isBlocked()){
            throw new IllegalStateException("Cannot follow this subscription");
        }
        if(isFollowing()){
            throw new IllegalStateException("Cannot follow this subscription");
        }
    }

    public void canUnFollow(){
        if(isBlocked()){
            throw new IllegalStateException("Cannot follow this subscription");
        }
        if(isUnFollowed()){
            throw new IllegalStateException("Cannot follow this subscription");
        }
    }

    public void canBlock(){
        if(isBlocked()){
            throw new IllegalStateException("Cannot block this subscription");
        }
    }


    public boolean isFollowing(){
        return this.followStatus.equals(FollowStatus.FOLLOWING);
    }

    public boolean isBlocked(){
        return this.followStatus.equals(FollowStatus.BLOCKED_BY_FOLLOWEE);
    }

    public boolean isUnFollowed(){
        return this.followStatus.equals(FollowStatus.UNFOLLOWED);
    }

    public void unfollow() {

        this.followStatus = FollowStatus.UNFOLLOWED;
        this.endAt = LocalDateTime.now();
    }

    public void blockFollowingUser() {

        this.followStatus = FollowStatus.BLOCKED_BY_FOLLOWEE;
        this.endAt = LocalDateTime.now();
    }

    public void follow() {

        this.followStatus = FollowStatus.FOLLOWING;
        this.startAt = LocalDateTime.now();
    }

}