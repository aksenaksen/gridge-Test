package com.example.instagram.subscription.application;

import com.example.instagram.subscription.application.dto.in.SubscriptionBlockCommand;
import com.example.instagram.subscription.application.dto.in.SubscriptionFollowCommand;
import com.example.instagram.subscription.application.dto.in.SubscriptionUnFollowCommand;
import com.example.instagram.subscription.domain.Subscription;
import com.example.instagram.subscription.infrastructor.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscriptionCommander {

    private final SubscriptionRepository subscriptionRepository;

    public void follow(SubscriptionFollowCommand command) {
        subscriptionRepository.findByFollowRelation_FollowerIdAndFollowRelationFolloweeId(command.followerId(), command.followeeId())
                .ifPresentOrElse((sub) -> {
                    sub.canFollow();
                    sub.follow();
                }, () -> {
                    Subscription subscription = Subscription.createSubscription(command.followerId(), command.followeeId());
                    subscriptionRepository.save(subscription);
                });
    }

    public void unfollow(SubscriptionUnFollowCommand command) {
        Subscription subscription = subscriptionRepository.findByFollowRelation_FollowerIdAndFollowRelationFolloweeId(command.followerId(), command.followeeId())
                .orElseThrow();

        subscription.canUnFollow();
        subscription.unfollow();
    }

    public void block(SubscriptionBlockCommand command) {
        Subscription subscription = subscriptionRepository.findByFollowRelation_FollowerIdAndFollowRelationFolloweeId(command.followerId(), command.userId())
                .orElseThrow();

        subscription.canBlock();
        subscription.blockFollowingUser();
    }
}
