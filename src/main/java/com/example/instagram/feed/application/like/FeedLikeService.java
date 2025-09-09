package com.example.instagram.feed.application.like;

import com.example.instagram.feed.application.feed.FeedFinder;
import com.example.instagram.feed.application.dto.in.LikeCommand;
import com.example.instagram.feed.application.dto.in.UnLikeCommand;
import com.example.instagram.feed.application.dto.out.ResponseFeedLikeCount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeedLikeService {

    private final FeedLikeCommander feedLikeCommander;
    private final LikeCountCommander likeCountCommander;

    private final FeedFinder feedFinder;
    private final LikeCountFinder likeCountFinder;

    @Transactional
    public void like(LikeCommand command){
        feedFinder.findById(command.feedId());
        feedLikeCommander.like(command);
        likeCountCommander.like(command);
    }

    @Transactional
    public void unlike(UnLikeCommand command){
        feedFinder.findById(command.feedId());
        feedLikeCommander.unlike(command);
        likeCountCommander.unLike(command);
    }

    @Transactional
    public ResponseFeedLikeCount findLikeCount(Long feedId){
        return ResponseFeedLikeCount.from(likeCountFinder.findByFeedId(feedId));
    }
}
