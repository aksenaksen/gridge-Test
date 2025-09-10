package com.example.instagram.feed.infrastructor;

import com.example.instagram.feed.domain.Feed;
import com.example.instagram.feed.domain.FeedImage;
import com.example.instagram.feed.domain.FeedStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FeedRepository extends JpaRepository<Feed, Long>, FeedRepositoryCustom {

    @EntityGraph(attributePaths = {"images"})
    Optional<Feed> findByFeedIdAndStatus(@Param("feedId") Long feedId, @Param("status") FeedStatus status);
    
    @Query("SELECT f FROM Feed f WHERE f.writerId = :writerId AND f.status = :status")
    Page<Feed> findByWriterIdAndStatus(@Param("writerId") Long writerId, @Param("status") FeedStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"images"})
    List<Feed> findAllByWriterIdAndStatus(Long writerId, FeedStatus status);

    List<Feed> findAllByOrderByFeedIdDesc();
}