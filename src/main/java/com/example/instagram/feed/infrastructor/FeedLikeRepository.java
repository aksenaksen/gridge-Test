package com.example.instagram.feed.infrastructor;

import com.example.instagram.feed.domain.FeedLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FeedLikeRepository extends JpaRepository<FeedLike,Long> {

    Optional<FeedLike> findByFeedId(Long id);

    Optional<FeedLike> findByFeedIdAndUserId(Long feedId, Long userId);
}
