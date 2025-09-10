package com.example.instagram.feed.presentation.in;

import com.example.instagram.common.Page;
import com.example.instagram.feed.domain.FeedStatus;

import java.time.LocalDate;

public record RequestFindFeedCondition(
        Long writerId,
        LocalDate date,
        FeedStatus status,
        Long pageSize,
        Long lastId
) {
    public Page toPage(){
        return new Page(
                pageSize,
                lastId
        );
    }
}
