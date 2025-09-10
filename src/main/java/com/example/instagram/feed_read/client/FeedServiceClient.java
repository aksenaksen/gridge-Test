package com.example.instagram.feed_read.client;

import com.example.instagram.common.Page;
import com.example.instagram.feed.application.feed.FeedService;
import com.example.instagram.feed.application.dto.out.ResponseFeedDto;
import com.example.instagram.feed_read.application.FeedClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FeedServiceClient implements FeedClient {

    private final FeedService feedService;

    @Override
    public List<ResponseFeedDto> read(List<Long> followingIds, Page page){
        return feedService.findAllFeedWithUserIds(followingIds, page);
    }

    @Override
    public List<Long> readIds(List<Long> followingIds, Page page){
        return feedService.findAllFeedIdsWithUserIds(followingIds, page);
    }

    @Override
    public ResponseFeedDto read(Long feedId){
        return feedService.findFeedById(feedId);
    }
}
