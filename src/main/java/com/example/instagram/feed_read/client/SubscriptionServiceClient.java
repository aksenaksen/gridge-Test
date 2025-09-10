package com.example.instagram.feed_read.client;

import com.example.instagram.feed_read.application.SubscriptionClient;
import com.example.instagram.subscription.application.SubscriptionService;
import com.example.instagram.subscription.application.dto.out.ResponseSubscriptionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SubscriptionServiceClient implements SubscriptionClient {

    private final SubscriptionService subscriptionService;

    @Override
    public List<Long> readSubscriptions(Long userId){
        return subscriptionService.findFollowingList(userId).stream()
                .map(ResponseSubscriptionDto::followeeId)
                .toList();
    }
}
