package com.example.instagram.subscription.application;

import com.example.instagram.subscription.application.dto.in.SubscriptionSearchCondition;
import com.example.instagram.subscription.domain.FollowStatus;
import com.example.instagram.subscription.domain.Subscription;
import com.example.instagram.subscription.infrastructor.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SubscriptionFinder {

    private final SubscriptionRepository subscriptionRepository;

    public List<Subscription> findByUserId(Long followerId){
        return subscriptionRepository.findAllByFollowRelation_FollowerId(followerId);
    }

    public List<Subscription> findAllWithCondition(SubscriptionSearchCondition condition , Long lastId, Long pageSize) {

        LocalDateTime startDate = null;
        LocalDateTime endDate = null;
        if(condition.date() != null){
            startDate  = condition.date().atStartOfDay();
            endDate  = condition.date().atStartOfDay().plusDays(1).minusSeconds(1);
        }

        return lastId == null ?
                subscriptionRepository.findSubscriptionsByConditions(condition.userId(), condition.status(), startDate, endDate, pageSize) :
                subscriptionRepository.findSubscriptionsByConditions(condition.userId(), condition.status(), startDate, endDate, pageSize, lastId);

    }

    public List<Long> findFollowingUserIds(Long followerId, FollowStatus status) {
        return subscriptionRepository.findFollowingUserIds(followerId, status);
    }
}
