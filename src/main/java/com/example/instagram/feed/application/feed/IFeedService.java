package com.example.instagram.feed.application.feed;

import com.example.instagram.feed.application.dto.in.FeedCreateCommand;
import com.example.instagram.feed.application.dto.in.FeedDeleteByAdminCommand;
import com.example.instagram.feed.application.dto.in.FeedDeleteCommand;
import com.example.instagram.feed.application.dto.in.FeedUpdateCommand;
import com.example.instagram.feed.application.dto.out.ResponseFeedDto;
import com.example.instagram.feed.domain.FeedStatus;
import com.example.instagram.feed.presentation.in.RequestFindFeedCondition;
import com.example.instagram.common.Page;

import java.util.List;

public interface IFeedService {
    
    void createFeed(FeedCreateCommand command);
    
    ResponseFeedDto findFeedById(Long feedId);
    
    List<ResponseFeedDto> findAllActiveFeedsWithWriterId(Long writerId, FeedStatus status);

    List<ResponseFeedDto> findAllFeedWithCondition(Page page, RequestFindFeedCondition condition);

    void updateFeed(FeedUpdateCommand command);

    void deleteFeed(FeedDeleteCommand command);

    void deleteFeedByAdmin(FeedDeleteByAdminCommand command);
}