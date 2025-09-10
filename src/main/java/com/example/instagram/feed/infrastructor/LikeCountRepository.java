package com.example.instagram.feed.infrastructor;

import com.example.instagram.feed.domain.LikeCount;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface LikeCountRepository extends JpaRepository<LikeCount, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<LikeCount> findLockedByFeedId(Long feedId);

    Optional<LikeCount> findByFeedId(Long feedId);
}
