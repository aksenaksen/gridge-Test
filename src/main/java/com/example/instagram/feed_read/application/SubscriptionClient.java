package com.example.instagram.feed_read.application;

import java.util.List;

public interface SubscriptionClient {
    List<Long> readSubscriptions(Long userId);
}
