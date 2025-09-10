package com.example.instagram.subscription.infrastructor;

import com.example.instagram.subscription.domain.FollowStatus;
import com.example.instagram.subscription.domain.QSubscription;
import com.example.instagram.subscription.domain.Subscription;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class SubscriptionRepositoryImpl implements SubscriptionRepositoryCustom {
    
    private final JPAQueryFactory queryFactory;
    
    @Override
    public List<Subscription> findSubscriptionsByConditions(Long userId, FollowStatus status, LocalDateTime startAt, LocalDateTime endAt, Long limit) {
        QSubscription subscription = QSubscription.subscription;

        BooleanBuilder builder = createCondition(null, userId, status, startAt, endAt, subscription);

        JPQLQuery<Subscription> query = queryFactory.selectFrom(subscription)
                .where(builder)
                .orderBy(subscription.subscriptionId.desc());

        if(limit != null) {
            query.limit(limit);
        }

        return query.fetch();
    }

    @Override
    public List<Subscription> findSubscriptionsByConditions(Long userId, FollowStatus status, LocalDateTime startAt, LocalDateTime endAt, Long limit, Long lastSubscriptionId) {
        QSubscription subscription = QSubscription.subscription;

        BooleanBuilder builder = createCondition(lastSubscriptionId, userId, status, startAt, endAt, subscription);

        JPQLQuery<Subscription> query = queryFactory.selectFrom(subscription)
                .where(builder)
                .orderBy(subscription.subscriptionId.desc());

        if(limit != null) {
            query.limit(limit);
        }

        return query.fetch();
    }
    
    @Override
    public List<Long> findFollowingUserIds(Long followerId, FollowStatus status) {
        QSubscription subscription = QSubscription.subscription;
        
        return queryFactory
                .select(subscription.followRelation.followeeId)
                .from(subscription)
                .where(
                    subscription.followRelation.followerId.eq(followerId)
                    .and(subscription.followStatus.eq(status))
                )
                .fetch();
    }

    private BooleanBuilder createCondition(Long lastSubscriptionId, Long userId, FollowStatus status, LocalDateTime startAt, LocalDateTime endAt, QSubscription subscription) {
        BooleanBuilder builder = new BooleanBuilder();

        if(lastSubscriptionId != null) {
            builder.and(subscription.subscriptionId.lt(lastSubscriptionId));
        }
        
        if (userId != null) {
            builder.and(
                subscription.followRelation.followerId.eq(userId)
                .or(subscription.followRelation.followeeId.eq(userId))
            );
        }

        if (status != null) {
            builder.and(subscription.followStatus.eq(status));
        }

        if (startAt != null) {
            builder.and(subscription.startAt.goe(startAt));
        }

        if (endAt != null) {
            builder.and(subscription.endAt.loe(endAt));
        }
        
        return builder;
    }
}