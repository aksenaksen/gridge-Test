package com.example.instagram.feed_read.application;

import com.example.instagram.common.events.*;
import com.example.instagram.feed.domain.Feed;
import com.example.instagram.feed_read.infrastructor.FeedQueryModel;
import com.example.instagram.feed_read.infrastructor.FeedQueryModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class FeedReadEventHandler {

    private final FeedQueryModelRepository feedQueryModelRepository;

    @EventListener
    public void handleLikeEvent(LikeEvent event) {
        feedQueryModelRepository.read(event.feedId()).ifPresent(feedQueryModel -> {
            feedQueryModel.incrementLikeCount();
            feedQueryModelRepository.update(feedQueryModel);
        });
    }

    @EventListener
    public void handleUnLikeEvent(UnLikeEvent event) {
        feedQueryModelRepository.read(event.feedId()).ifPresent(feedQueryModel -> {
            feedQueryModel.decrementLikeCount();
            feedQueryModelRepository.update(feedQueryModel);
        });
    }

    @EventListener
    public void handleDeleteCommentEvent(DeleteCommentEvent event) {
        feedQueryModelRepository.read(event.feedId()).ifPresent(feedQueryModel -> {
            feedQueryModel.decrementCommentCount();
            feedQueryModelRepository.update(feedQueryModel);
        });
    }

    @EventListener
    public void handleUpdateFeedContentEvent(UpdateFeedContentEvent event) {
        feedQueryModelRepository.read(event.feedId()).ifPresent(feedQueryModel -> {
            feedQueryModel.updateContent(event.content());
            feedQueryModelRepository.update(feedQueryModel);
        });
    }

    @EventListener
    public void handleDeleteFeedEvent(DeleteFeedEvent event) {
        feedQueryModelRepository.delete(event.feedId());
    }

    @Transactional
    @EventListener
    public void handleReportBlockEvent(ReportBlockEvent event) {
        feedQueryModelRepository.delete(event.feedId());
    }
}