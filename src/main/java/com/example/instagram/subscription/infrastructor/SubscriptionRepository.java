package com.example.instagram.subscription.infrastructor;

import com.example.instagram.subscription.domain.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long>, SubscriptionRepositoryCustom {
    List<Subscription> findAllByFollowRelation_FollowerId(Long followerId);
    Optional<Subscription> findByFollowRelation_FollowerIdAndFollowRelationFolloweeId(Long followerId, Long followeeId);
}
