package com.example.instagram.feed.application.feed;

import com.example.instagram.feed.application.dto.in.FeedCreateCommand;
import com.example.instagram.feed.application.dto.in.FeedDeleteByAdminCommand;
import com.example.instagram.feed.application.dto.in.FeedDeleteCommand;
import com.example.instagram.feed.application.dto.in.FeedUpdateCommand;
import com.example.instagram.feed.application.dto.out.ResponseFeedDto;
import com.example.instagram.feed.domain.Feed;
import com.example.instagram.feed.domain.FeedStatus;
import com.example.instagram.feed.presentation.in.RequestFindFeedCondition;
import com.example.instagram.common.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService implements IFeedService {
    
    private final FeedFinder feedFinder;
    private final FeedCommander feedCommander;
    
    @Override
    @Transactional
    public void createFeed(FeedCreateCommand command) {
        feedCommander.createFeed(command);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ResponseFeedDto findFeedById(Long feedId) {
        Feed feed = feedFinder.findById(feedId);
        return ResponseFeedDto.from(feed);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResponseFeedDto> findAllActiveFeedsWithWriterId(Long writerId, FeedStatus status) {
        return feedFinder.findAllByWriterId(writerId, status).stream()
                .map(ResponseFeedDto::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResponseFeedDto> findAllFeedWithCondition(Page page, RequestFindFeedCondition condition) {
        return feedFinder.findAllWithCondition(condition,
                        page == null ? null : page.lastId(),
                        page == null ? null : page.pageSize()).stream()
                .map(ResponseFeedDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ResponseFeedDto> findAllFeedWithUserIds(List<Long> userIds, Page page) {
        return feedFinder.findByFollowingIds(userIds, page.lastId(), page.pageSize()).stream()
                .map(ResponseFeedDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Long> findAllFeedIdsWithUserIds(List<Long> userIds,Page page) {
        return feedFinder.findByFollowingIds(userIds, page.lastId(), page.pageSize()).stream()
                .map(Feed::getFeedId)
                .toList();
    }

    
    @Override
    @Transactional
    public void updateFeed(FeedUpdateCommand command) {
        feedCommander.updateFeed(command);
    }

    @Override
    @Transactional
    public void deleteFeed(FeedDeleteCommand command) {
        feedCommander.deleteFeed(command);
    }

    @Override
    @Transactional
    public void deleteFeedByAdmin(FeedDeleteByAdminCommand command) {
        feedCommander.deleteFeedByAdmin(command);
    }
}