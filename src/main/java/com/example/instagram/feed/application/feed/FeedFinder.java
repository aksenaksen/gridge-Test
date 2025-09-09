package com.example.instagram.feed.application.feed;

import com.example.instagram.feed.domain.Feed;
import com.example.instagram.feed.domain.FeedStatus;
import com.example.instagram.feed.exception.NotFoundFeedException;
import com.example.instagram.feed.infrastructor.FeedRepository;
import com.example.instagram.feed.presentation.in.RequestFindFeedCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedFinder {
    
    private final FeedRepository feedRepository;
    
    public Feed findById(Long feedId) {
        return feedRepository.findById(feedId)
                .orElseThrow(NotFoundFeedException::new);
    }
    
    public Feed findByIdAndStatusWithImages(Long feedId , FeedStatus status) {
        return feedRepository.findByFeedIdAndStatus(feedId, status)
                .orElseThrow(NotFoundFeedException::new);
    }
    
    public List<Feed> findAllByWriterId(Long writerId, FeedStatus status) {
        return feedRepository.findAllByWriterIdAndStatus(writerId, status);
    }

    public List<Feed> findAllWithCondition(RequestFindFeedCondition condition , Long lastId, Long pageSize) {

        LocalDateTime startDate = null;
        LocalDateTime endDate = null;
        if(condition.date() != null){
            startDate  = condition.date().atStartOfDay();
            endDate  = condition.date().atStartOfDay().plusDays(1).minusSeconds(1);
        }

        return lastId == null ?
                feedRepository.findFeedsByConditions(condition.writerId(), condition.status(), startDate, endDate, pageSize) :
                feedRepository.findFeedsByConditions(condition.writerId(), condition.status(), startDate, endDate, pageSize, lastId);

    }

    public List<Feed> findByFollowingIds(List<Long> followIds, Long lastId, Long pageSize) {
        return lastId == null ?
                feedRepository.findRecentFeedsByFollowing(followIds, pageSize) :
                feedRepository.findRecentFeedsByFollowing(followIds, pageSize, lastId);
    }
}