package com.example.instagram.feed_read.application;

import com.example.instagram.common.Page;
import com.example.instagram.feed_read.infrastructor.FeedQueryModel;
import com.example.instagram.feed_read.infrastructor.FeedQueryModelRepository;
import com.example.instagram.feed_read.application.out.ResponseFeedRead;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedReadService {

    private final SubscriptionClient subscriptionClient;
    private final FeedClient feedServiceClient;
    private final CommentClient commentClient;
    private final LikeClient likeClient;

    private final FeedQueryModelRepository feedQueryModelRepository;

    public List<ResponseFeedRead> findAll(Long userId, Page page) {
        List<Long> followingList = getFollowingList(userId);
        List<Long> feedIds = feedServiceClient.readIds(followingList, page);
        return feedQueryModelRepository.readAll(feedIds).entrySet().stream()
                .map((entry) -> {

                        Long feedId = entry.getKey();
                        FeedQueryModel data = entry.getValue();

                        if(data == null) {
                            data = fetch(feedId);
                            feedQueryModelRepository.create(data, Duration.ofHours(5));
                        }
                        return data;
                    }
                )
                .map(ResponseFeedRead::from)
                .toList();
    }

    public FeedQueryModel fetch(Long feedId){
        return FeedQueryModel.of(
                feedServiceClient.read(feedId),
                likeClient.count(feedId),
                commentClient.getCount(feedId)
        );
    }


    public List<FeedQueryModel> fetch(Long userId, Page page){
        List<Long> followingList = getFollowingList(userId);
        return feedServiceClient.read(followingList, page).stream()
                .map((feed) -> FeedQueryModel.of(
                        feed,
                        likeClient.count(feed.feedId()),
                        commentClient.getCount(feed.feedId())))
                .toList();
    }

    public List<Long> getFollowingList(Long userId){
        return subscriptionClient.readSubscriptions(userId);
    }
}
