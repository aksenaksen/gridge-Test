package com.example.instagram.feed.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class FeedLikeTest {

    private final Long FEED_ID = 1L;
    private final Long USER_ID = 1L;

    @Nested
    @DisplayName("FeedLike 생성 테스트")
    class CreateFeedLikeTest {

        @Test
        @DisplayName("정적 팩토리 메소드를 통한 FeedLike 생성 성공")
        void createFeedLikeSuccess() {
            // when
            FeedLike feedLike = FeedLike.create(FEED_ID, USER_ID);

            // then
            assertThat(feedLike.getFeedId()).isEqualTo(FEED_ID);
            assertThat(feedLike.getUserId()).isEqualTo(USER_ID);
        }

        @Test
        @DisplayName("다른 사용자 ID로 FeedLike 생성 성공")
        void createFeedLikeWithDifferentUserIdSuccess() {
            // given
            Long differentUserId = 2L;

            // when
            FeedLike feedLike = FeedLike.create(FEED_ID, differentUserId);

            // then
            assertThat(feedLike.getFeedId()).isEqualTo(FEED_ID);
            assertThat(feedLike.getUserId()).isEqualTo(differentUserId);
        }

        @Test
        @DisplayName("다른 피드 ID로 FeedLike 생성 성공")
        void createFeedLikeWithDifferentFeedIdSuccess() {
            // given
            Long differentFeedId = 2L;

            // when
            FeedLike feedLike = FeedLike.create(differentFeedId, USER_ID);

            // then
            assertThat(feedLike.getFeedId()).isEqualTo(differentFeedId);
            assertThat(feedLike.getUserId()).isEqualTo(USER_ID);
        }
    }

    @Nested
    @DisplayName("FeedLike 속성 검증 테스트")
    class FeedLikeValidationTest {

        @Test
        @DisplayName("null 피드 ID로 FeedLike 생성")
        void createFeedLikeWithNullFeedId() {
            // when
            FeedLike feedLike = FeedLike.create(null, USER_ID);

            // then
            assertThat(feedLike.getFeedId()).isNull();
            assertThat(feedLike.getUserId()).isEqualTo(USER_ID);
        }

        @Test
        @DisplayName("null 사용자 ID로 FeedLike 생성")
        void createFeedLikeWithNullUserId() {
            // when
            FeedLike feedLike = FeedLike.create(FEED_ID, null);

            // then
            assertThat(feedLike.getFeedId()).isEqualTo(FEED_ID);
            assertThat(feedLike.getUserId()).isNull();
        }

        @Test
        @DisplayName("0 값으로 FeedLike 생성")
        void createFeedLikeWithZeroValues() {
            // when
            FeedLike feedLike = FeedLike.create(0L, 0L);

            // then
            assertThat(feedLike.getFeedId()).isEqualTo(0L);
            assertThat(feedLike.getUserId()).isEqualTo(0L);
        }

        @Test
        @DisplayName("음수 값으로 FeedLike 생성")
        void createFeedLikeWithNegativeValues() {
            // when
            FeedLike feedLike = FeedLike.create(-1L, -1L);

            // then
            assertThat(feedLike.getFeedId()).isEqualTo(-1L);
            assertThat(feedLike.getUserId()).isEqualTo(-1L);
        }

        @Test
        @DisplayName("큰 값으로 FeedLike 생성")
        void createFeedLikeWithLargeValues() {
            // given
            Long largeFeedId = Long.MAX_VALUE;
            Long largeUserId = Long.MAX_VALUE;

            // when
            FeedLike feedLike = FeedLike.create(largeFeedId, largeUserId);

            // then
            assertThat(feedLike.getFeedId()).isEqualTo(largeFeedId);
            assertThat(feedLike.getUserId()).isEqualTo(largeUserId);
        }
    }

    @Nested
    @DisplayName("FeedLike 동등성 테스트")
    class FeedLikeEqualityTest {

        @Test
        @DisplayName("동일한 값으로 생성된 FeedLike 객체 비교")
        void compareFeedLikesWithSameValues() {
            // given
            FeedLike feedLike1 = FeedLike.create(FEED_ID, USER_ID);
            FeedLike feedLike2 = FeedLike.create(FEED_ID, USER_ID);

            // then
            assertThat(feedLike1.getFeedId()).isEqualTo(feedLike2.getFeedId());
            assertThat(feedLike1.getUserId()).isEqualTo(feedLike2.getUserId());
        }

        @Test
        @DisplayName("다른 값으로 생성된 FeedLike 객체 비교")
        void compareFeedLikesWithDifferentValues() {
            // given
            FeedLike feedLike1 = FeedLike.create(FEED_ID, USER_ID);
            FeedLike feedLike2 = FeedLike.create(2L, 2L);

            // then
            assertThat(feedLike1.getFeedId()).isNotEqualTo(feedLike2.getFeedId());
            assertThat(feedLike1.getUserId()).isNotEqualTo(feedLike2.getUserId());
        }
    }

    @Nested
    @DisplayName("FeedLike ID 테스트")
    class FeedLikeIdTest {

        @Test
        @DisplayName("새로 생성된 FeedLike의 ID는 null")
        void newFeedLikeIdIsNull() {
            // when
            FeedLike feedLike = FeedLike.create(FEED_ID, USER_ID);

            // then
            assertThat(feedLike.getFeedLikeId()).isNull();
        }
    }
}