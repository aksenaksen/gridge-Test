package com.example.instagram.feed.infrastructor;

import com.example.instagram.feed.domain.Feed;
import com.example.instagram.feed.domain.FeedStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface FeedRepositoryCustom {

    List<Feed> findFeedsByConditions(Long writerId, FeedStatus status, LocalDateTime startDate, LocalDateTime endDate, Long limit);

    List<Feed> findFeedsByConditions(Long writerId, FeedStatus status, LocalDateTime startDate, LocalDateTime endDate, Long limit, Long lastFeedId);

    List<Feed> findRecentFeedsByFollowing(List<Long> followingUserIds, Long limit);
    
    List<Feed> findRecentFeedsByFollowing(List<Long> followingUserIds, Long limit, Long lastFeedId);
    
}