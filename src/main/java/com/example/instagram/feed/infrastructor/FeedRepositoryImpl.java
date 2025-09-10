package com.example.instagram.feed.infrastructor;

import com.example.instagram.feed.domain.Feed;
import com.example.instagram.feed.domain.FeedStatus;
import com.example.instagram.feed.domain.QFeed;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class FeedRepositoryImpl implements FeedRepositoryCustom {
    
    private final JPAQueryFactory queryFactory;
    
    @Override
    public List<Feed> findFeedsByConditions(Long writerId, FeedStatus status, LocalDateTime startDate, LocalDateTime endDate, Long limit) {
        QFeed feed = QFeed.feed;

        BooleanBuilder builder = createCondition(null ,writerId, status, startDate, endDate, feed);

        JPQLQuery<Feed> query = queryFactory.selectFrom(feed)
                .where(builder)
                .orderBy(feed.feedId.desc());

        if(limit != null) {
            query.limit(limit);
        }

        return query.fetch();
    }

    @Override
    public List<Feed> findFeedsByConditions(Long writerId, FeedStatus status, LocalDateTime startDate, LocalDateTime endDate, Long limit, Long lastFeedId) {
        QFeed feed = QFeed.feed;

        BooleanBuilder builder = createCondition(lastFeedId, writerId, status, startDate, endDate, feed);

        JPQLQuery<Feed> query = queryFactory.selectFrom(feed)
                .where(builder)
                .orderBy(feed.feedId.desc());

        if(limit != null) {
            query.limit(limit);
        }

        return query.fetch();
    }

    @Override
    public List<Feed> findRecentFeedsByFollowing(List<Long> followingUserIds, Long limit) {
        QFeed feed = QFeed.feed;
        
        if (followingUserIds.isEmpty()) {
            return List.of();
        }
        
        JPQLQuery<Feed> query = queryFactory
                .selectFrom(feed)
                .where(
                    feed.writerId.in(followingUserIds)
                    .and(feed.status.eq(FeedStatus.ACTIVE))
                )
                .orderBy(feed.feedId.desc());
                
        if (limit != null) {
            query.limit(limit);
        }
        
        return query.fetch();
    }
    
    @Override
    public List<Feed> findRecentFeedsByFollowing(List<Long> followingUserIds, Long limit, Long lastFeedId) {
        QFeed feed = QFeed.feed;
        
        if (followingUserIds.isEmpty()) {
            return List.of();
        }
        
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(feed.writerId.in(followingUserIds))
               .and(feed.status.eq(FeedStatus.ACTIVE));
               
        if (lastFeedId != null) {
            builder.and(feed.feedId.lt(lastFeedId));
        }
        
        JPQLQuery<Feed> query = queryFactory
                .selectFrom(feed)
                .where(builder)
                .orderBy(feed.feedId.desc());
                
        if (limit != null) {
            query.limit(limit);
        }
        
        return query.fetch();
    }

    private BooleanBuilder createCondition(Long lastFeedId, Long writerId, FeedStatus status, LocalDateTime startDate, LocalDateTime endDate, QFeed feed) {
        BooleanBuilder builder = new BooleanBuilder();

        if(lastFeedId != null) {
            builder.and(feed.feedId.lt(lastFeedId));
        }
        if (writerId != null) {
            builder.and(feed.writerId.eq(writerId));
        }

        if (status != null) {
            builder.and(feed.status.eq(status));
        }

        if (startDate != null) {
            builder.and(feed.createdAt.goe(startDate));
        }

        if (endDate != null) {
            builder.and(feed.createdAt.loe(endDate));
        }
        return builder;
    }
}