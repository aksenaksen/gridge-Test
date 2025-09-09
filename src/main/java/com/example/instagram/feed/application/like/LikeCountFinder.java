package com.example.instagram.feed.application.like;

import com.example.instagram.feed.domain.LikeCount;
import com.example.instagram.feed.infrastructor.LikeCountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeCountFinder {

    private final LikeCountRepository likeCountRepository;

    public LikeCount findByFeedId(Long feedId){
        return likeCountRepository.findByFeedId(feedId)
                .orElseThrow();
    }
}
