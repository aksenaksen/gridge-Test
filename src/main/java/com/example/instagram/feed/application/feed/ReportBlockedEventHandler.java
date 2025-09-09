package com.example.instagram.feed.application.feed;

import com.example.instagram.common.events.ReportBlockEvent;
import com.example.instagram.feed.domain.Feed;
import com.example.instagram.feed.infrastructor.FeedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ReportBlockedEventHandler {

    private final FeedFinder feedFinder;

    @Transactional
    @EventListener
    public void handleReportBlockEvent(ReportBlockEvent event) {
        Feed feed =feedFinder.findById(event.feedId());
        feed.deleteByAdmin();
    }
}
