package com.example.instagram.feed_read.application;

import com.example.instagram.common.Page;
import com.example.instagram.feed.application.dto.out.ResponseFeedDto;

import java.util.List;

public interface FeedClient {
    List<ResponseFeedDto> read(List<Long> followingIds, Page page);

    List<Long> readIds(List<Long> followingIds, Page page);

    ResponseFeedDto read(Long feedId);
}
