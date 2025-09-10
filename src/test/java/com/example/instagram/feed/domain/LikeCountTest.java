package com.example.instagram.feed.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class LikeCountTest {

    private final Long FEED_ID = 1L;
    private final Long INITIAL_COUNT = 5L;

    @Nested
    @DisplayName("LikeCount 생성 테스트")
    class CreateLikeCountTest {

        @Test
        @DisplayName("정적 팩토리 메소드를 통한 LikeCount 생성 성공")
        void createLikeCountSuccess() {
            // when
            LikeCount likeCount = LikeCount.create(FEED_ID, INITIAL_COUNT);

            // then
            assertThat(likeCount.getFeedId()).isEqualTo(FEED_ID);
            assertThat(likeCount.getLikeCount()).isEqualTo(INITIAL_COUNT);
        }

        @Test
        @DisplayName("0으로 LikeCount 생성 성공")
        void createLikeCountWithZeroSuccess() {
            // when
            LikeCount likeCount = LikeCount.create(FEED_ID, 0L);

            // then
            assertThat(likeCount.getFeedId()).isEqualTo(FEED_ID);
            assertThat(likeCount.getLikeCount()).isEqualTo(0L);
        }

        @Test
        @DisplayName("큰 값으로 LikeCount 생성 성공")
        void createLikeCountWithLargeValueSuccess() {
            // given
            Long largeCount = Long.MAX_VALUE;

            // when
            LikeCount likeCount = LikeCount.create(FEED_ID, largeCount);

            // then
            assertThat(likeCount.getFeedId()).isEqualTo(FEED_ID);
            assertThat(likeCount.getLikeCount()).isEqualTo(largeCount);
        }
    }

    @Nested
    @DisplayName("LikeCount 증가 테스트")
    class IncrementCountTest {

        private LikeCount likeCount;

        @BeforeEach
        void setUp() {
            likeCount = LikeCount.create(FEED_ID, INITIAL_COUNT);
        }

        @Test
        @DisplayName("좋아요 수 증가 성공")
        void incrementCountSuccess() {
            // when
            likeCount.incrementCount();

            // then
            assertThat(likeCount.getLikeCount()).isEqualTo(INITIAL_COUNT + 1);
        }

        @Test
        @DisplayName("좋아요 수 여러 번 증가 성공")
        void incrementCountMultipleTimesSuccess() {
            // when
            likeCount.incrementCount();
            likeCount.incrementCount();
            likeCount.incrementCount();

            // then
            assertThat(likeCount.getLikeCount()).isEqualTo(INITIAL_COUNT + 3);
        }

        @Test
        @DisplayName("0에서 좋아요 수 증가 성공")
        void incrementCountFromZeroSuccess() {
            // given
            LikeCount zeroLikeCount = LikeCount.create(FEED_ID, 0L);

            // when
            zeroLikeCount.incrementCount();

            // then
            assertThat(zeroLikeCount.getLikeCount()).isEqualTo(1L);
        }

        @Test
        @DisplayName("최대값에서 좋아요 수 증가 시 오버플로우 발생")
        void incrementCountOverflowHandling() {
            // given
            LikeCount maxLikeCount = LikeCount.create(FEED_ID, Long.MAX_VALUE);

            // when
            maxLikeCount.incrementCount();

            // then
            // Long.MAX_VALUE + 1 = Long.MIN_VALUE (오버플로우)
            assertThat(maxLikeCount.getLikeCount()).isEqualTo(Long.MIN_VALUE);
        }
    }

    @Nested
    @DisplayName("LikeCount 감소 테스트")
    class DecrementCountTest {

        private LikeCount likeCount;

        @BeforeEach
        void setUp() {
            likeCount = LikeCount.create(FEED_ID, INITIAL_COUNT);
        }

        @Test
        @DisplayName("좋아요 수 감소 성공")
        void decrementCountSuccess() {
            // when
            likeCount.decrementCount();

            // then
            assertThat(likeCount.getLikeCount()).isEqualTo(INITIAL_COUNT - 1);
        }

        @Test
        @DisplayName("좋아요 수 여러 번 감소 성공")
        void decrementCountMultipleTimesSuccess() {
            // when
            likeCount.decrementCount();
            likeCount.decrementCount();
            likeCount.decrementCount();

            // then
            assertThat(likeCount.getLikeCount()).isEqualTo(INITIAL_COUNT - 3);
        }

        @Test
        @DisplayName("1에서 좋아요 수 감소 성공")
        void decrementCountFromOneSuccess() {
            // given
            LikeCount oneLikeCount = LikeCount.create(FEED_ID, 1L);

            // when
            oneLikeCount.decrementCount();

            // then
            assertThat(oneLikeCount.getLikeCount()).isEqualTo(0L);
        }

        @Test
        @DisplayName("0에서 좋아요 수 감소 시 0 유지")
        void decrementCountFromZeroStaysZero() {
            // given
            LikeCount zeroLikeCount = LikeCount.create(FEED_ID, 0L);

            // when
            zeroLikeCount.decrementCount();

            // then
            assertThat(zeroLikeCount.getLikeCount()).isEqualTo(0L);
        }

        @Test
        @DisplayName("0에서 여러 번 감소 시도해도 0 유지")
        void decrementCountFromZeroMultipleTimesStaysZero() {
            // given
            LikeCount zeroLikeCount = LikeCount.create(FEED_ID, 0L);

            // when
            zeroLikeCount.decrementCount();
            zeroLikeCount.decrementCount();
            zeroLikeCount.decrementCount();

            // then
            assertThat(zeroLikeCount.getLikeCount()).isEqualTo(0L);
        }
    }

    @Nested
    @DisplayName("LikeCount 복합 연산 테스트")
    class CombinedOperationTest {

        private LikeCount likeCount;

        @BeforeEach
        void setUp() {
            likeCount = LikeCount.create(FEED_ID, 0L);
        }

        @Test
        @DisplayName("증가와 감소 연산 조합")
        void combinedIncrementAndDecrementOperations() {
            // when
            likeCount.incrementCount(); // 1
            likeCount.incrementCount(); // 2
            likeCount.decrementCount(); // 1
            likeCount.incrementCount(); // 2
            likeCount.decrementCount(); // 1
            likeCount.decrementCount(); // 0

            // then
            assertThat(likeCount.getLikeCount()).isEqualTo(0L);
        }

        @Test
        @DisplayName("증가 후 0 미만으로 감소 시도")
        void incrementThenDecrementBelowZero() {
            // when
            likeCount.incrementCount(); // 1
            likeCount.decrementCount(); // 0
            likeCount.decrementCount(); // 0 (변화 없음)
            likeCount.decrementCount(); // 0 (변화 없음)

            // then
            assertThat(likeCount.getLikeCount()).isEqualTo(0L);
        }
    }

    @Nested
    @DisplayName("LikeCount 속성 검증 테스트")
    class LikeCountValidationTest {

        @Test
        @DisplayName("null 피드 ID로 LikeCount 생성")
        void createLikeCountWithNullFeedId() {
            // when
            LikeCount likeCount = LikeCount.create(null, INITIAL_COUNT);

            // then
            assertThat(likeCount.getFeedId()).isNull();
            assertThat(likeCount.getLikeCount()).isEqualTo(INITIAL_COUNT);
        }

        @Test
        @DisplayName("null 좋아요 수로 LikeCount 생성")
        void createLikeCountWithNullCount() {
            // when
            LikeCount likeCount = LikeCount.create(FEED_ID, null);

            // then
            assertThat(likeCount.getFeedId()).isEqualTo(FEED_ID);
            assertThat(likeCount.getLikeCount()).isNull();
        }

        @Test
        @DisplayName("음수 좋아요 수로 LikeCount 생성")
        void createLikeCountWithNegativeCount() {
            // when
            LikeCount likeCount = LikeCount.create(FEED_ID, -5L);

            // then
            assertThat(likeCount.getFeedId()).isEqualTo(FEED_ID);
            assertThat(likeCount.getLikeCount()).isEqualTo(-5L);
        }
    }
}