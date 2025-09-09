package com.example.instagram.feed.application.feed;

import com.example.instagram.common.util.Snowflake;
import com.example.instagram.feed.application.dto.in.FeedCreateCommand;
import com.example.instagram.feed.application.dto.in.FeedDeleteByAdminCommand;
import com.example.instagram.feed.application.dto.in.FeedDeleteCommand;
import com.example.instagram.feed.application.dto.in.FeedUpdateCommand;
import com.example.instagram.feed.domain.Feed;
import com.example.instagram.feed.domain.FeedStatus;
import com.example.instagram.feed.exception.NotFoundFeedException;
import com.example.instagram.feed.infrastructor.FeedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class FeedCommander {
    
    private final FeedRepository feedRepository;
    private final Snowflake snowflake = new Snowflake();
    
    public void createFeed(FeedCreateCommand command) {
        Feed feed = Feed.createFeed(snowflake.nextId(), command.writerId(), command.content(), FeedStatus.ACTIVE);
        
        feed.addImages(command.imageUrls());

        feedRepository.save(feed);
    }
    
    public void updateFeed(FeedUpdateCommand command) {
        Feed feed = feedRepository.findById(command.feedId())
                .orElseThrow();
        
        feed.validateCanBeUpdated(command.userId());
        
        if (command.content() != null) {
            feed.updateContent(command.content());
        }
        
        if (command.imageUrls() != null) {
            feed.updateImages(command.imageUrls());
        }
    }
    
    public void deleteFeed(FeedDeleteCommand command) {
        Feed feed = feedRepository.findById(command.feedId())
                .orElseThrow(NotFoundFeedException::new);

        feed.delete(command.userId());
    }
    
    public void deleteFeedByAdmin(FeedDeleteByAdminCommand command) {
        Feed feed = feedRepository.findById(command.feedId())
                        .orElseThrow(NotFoundFeedException::new);

        feed.deleteByAdmin();
    }
    
}