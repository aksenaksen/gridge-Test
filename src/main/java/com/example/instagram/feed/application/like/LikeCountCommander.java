package com.example.instagram.feed.application.like;

import com.example.instagram.feed.application.dto.in.LikeCommand;
import com.example.instagram.feed.application.dto.in.UnLikeCommand;
import com.example.instagram.feed.domain.LikeCount;
import com.example.instagram.feed.infrastructor.LikeCountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeCountCommander {

    private final LikeCountRepository likeCountRepository;

    public void like(LikeCommand command){
        LikeCount likeCount = likeCountRepository.findLockedByFeedId(command.feedId())
                .orElseGet(() -> LikeCount.create(command.feedId(), 0L));
        likeCount.incrementCount();
        likeCountRepository.save(likeCount);
    }

    public void unLike(UnLikeCommand command){
        likeCountRepository.findLockedByFeedId(command.feedId())
                .ifPresent((like) -> {
                    like.decrementCount();
                    likeCountRepository.save(like);
                });
    }
}
