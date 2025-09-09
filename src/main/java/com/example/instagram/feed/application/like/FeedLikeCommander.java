package com.example.instagram.feed.application.like;

import com.example.instagram.feed.application.dto.in.LikeCommand;
import com.example.instagram.feed.application.dto.in.UnLikeCommand;
import com.example.instagram.feed.domain.FeedLike;
import com.example.instagram.feed.infrastructor.FeedLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeedLikeCommander {

    private final FeedLikeRepository feedLikeRepository;

    public void like(LikeCommand command){
        FeedLike feedLike = FeedLike.create(command.feedId(),command.userId());
        feedLikeRepository.save(feedLike);
    }

    public void unlike(UnLikeCommand command){
        feedLikeRepository.findByFeedIdAndUserId(command.feedId(),command.userId())
                .ifPresent((feedLike -> {
                    feedLike.publishUnlikeEvent();
                    feedLikeRepository.delete(feedLike);
                }));
    }
}
