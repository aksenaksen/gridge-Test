package com.example.instagram.feed.application;

import com.example.instagram.feed.application.feed.FeedFinder;
import com.example.instagram.feed.domain.Feed;
import com.example.instagram.feed.domain.FeedStatus;
import com.example.instagram.feed.exception.NotFoundFeedException;
import com.example.instagram.feed.infrastructor.FeedRepository;
import com.example.instagram.feed.presentation.in.RequestFindFeedCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FeedFinderTest {

    @Mock
    private FeedRepository feedRepository;

    @InjectMocks
    private FeedFinder feedFinder;

    private final Long FEED_ID = 1L;
    private final Long WRITER_ID = 1L;
    private final Long PAGE_SIZE = 10L;
    private Feed mockFeed;
    private List<Feed> mockFeeds;

    @BeforeEach
    void setUp() {
        mockFeed = Feed.createFeed(FEED_ID, WRITER_ID, "테스트 내용", FeedStatus.ACTIVE);
        mockFeeds = Arrays.asList(mockFeed, 
                                 Feed.createFeed(2L, WRITER_ID, "테스트 내용 2", FeedStatus.ACTIVE));
    }

    @Nested
    @DisplayName("FeedFinder ID로 피드 찾기 테스트")
    class FindByIdTest {

        @Test
        @DisplayName("ID로 피드 찾기 성공")
        void findByIdSuccess() {
            // given
            given(feedRepository.findById(FEED_ID)).willReturn(Optional.of(mockFeed));

            // when
            Feed foundFeed = feedFinder.findById(FEED_ID);

            // then
            assertThat(foundFeed).isEqualTo(mockFeed);
            verify(feedRepository).findById(FEED_ID);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 피드 찾기 시 예외 발생")
        void findByIdNotFoundThrowsException() {
            // given
            given(feedRepository.findById(FEED_ID)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> feedFinder.findById(FEED_ID))
                    .isInstanceOf(NotFoundFeedException.class);
            verify(feedRepository).findById(FEED_ID);
        }

        @Test
        @DisplayName("null ID로 피드 찾기 시 예외 발생")
        void findByNullIdThrowsException() {
            // given
            given(feedRepository.findById(null)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> feedFinder.findById(null))
                    .isInstanceOf(NotFoundFeedException.class);
        }

        @Test
        @DisplayName("0 ID로 피드 찾기")
        void findByZeroId() {
            // given
            given(feedRepository.findById(0L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> feedFinder.findById(0L))
                    .isInstanceOf(NotFoundFeedException.class);
        }

        @Test
        @DisplayName("음수 ID로 피드 찾기")
        void findByNegativeId() {
            // given
            given(feedRepository.findById(-1L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> feedFinder.findById(-1L))
                    .isInstanceOf(NotFoundFeedException.class);
        }
    }

    @Nested
    @DisplayName("FeedFinder ID와 상태로 피드 찾기 테스트")
    class FindByIdAndStatusTest {

        @Test
        @DisplayName("ID와 상태로 피드 찾기 성공")
        void findByIdAndStatusSuccess() {
            // given
            given(feedRepository.findByFeedIdAndStatus(FEED_ID, FeedStatus.ACTIVE))
                    .willReturn(Optional.of(mockFeed));

            // when
            Feed foundFeed = feedFinder.findByIdAndStatusWithImages(FEED_ID, FeedStatus.ACTIVE);

            // then
            assertThat(foundFeed).isEqualTo(mockFeed);
            verify(feedRepository).findByFeedIdAndStatus(FEED_ID, FeedStatus.ACTIVE);
        }

        @Test
        @DisplayName("존재하지 않는 ID와 상태로 피드 찾기 시 예외 발생")
        void findByIdAndStatusNotFoundThrowsException() {
            // given
            given(feedRepository.findByFeedIdAndStatus(FEED_ID, FeedStatus.ACTIVE))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> feedFinder.findByIdAndStatusWithImages(FEED_ID, FeedStatus.ACTIVE))
                    .isInstanceOf(NotFoundFeedException.class);
        }

        @Test
        @DisplayName("다른 상태로 피드 찾기")
        void findByIdAndDifferentStatus() {
            // given
            given(feedRepository.findByFeedIdAndStatus(FEED_ID, FeedStatus.USER_DELETED))
                    .willReturn(Optional.of(mockFeed));

            // when
            Feed foundFeed = feedFinder.findByIdAndStatusWithImages(FEED_ID, FeedStatus.USER_DELETED);

            // then
            assertThat(foundFeed).isEqualTo(mockFeed);
        }

        @Test
        @DisplayName("null 상태로 피드 찾기")
        void findByIdAndNullStatus() {
            // given
            given(feedRepository.findByFeedIdAndStatus(FEED_ID, null))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> feedFinder.findByIdAndStatusWithImages(FEED_ID, null))
                    .isInstanceOf(NotFoundFeedException.class);
        }
    }

    @Nested
    @DisplayName("FeedFinder 작성자 ID로 피드 찾기 테스트")
    class FindAllByWriterIdTest {

        @Test
        @DisplayName("작성자 ID로 피드 목록 찾기 성공")
        void findAllByWriterIdSuccess() {
            // given
            given(feedRepository.findAllByWriterIdAndStatus(WRITER_ID, FeedStatus.ACTIVE))
                    .willReturn(mockFeeds);

            // when
            List<Feed> foundFeeds = feedFinder.findAllByWriterId(WRITER_ID, FeedStatus.ACTIVE);

            // then
            assertThat(foundFeeds).isEqualTo(mockFeeds);
            assertThat(foundFeeds).hasSize(2);
            verify(feedRepository).findAllByWriterIdAndStatus(WRITER_ID, FeedStatus.ACTIVE);
        }

        @Test
        @DisplayName("존재하지 않는 작성자 ID로 피드 찾기 시 빈 목록 반환")
        void findAllByNonExistentWriterIdReturnsEmptyList() {
            // given
            Long nonExistentWriterId = 999L;
            given(feedRepository.findAllByWriterIdAndStatus(nonExistentWriterId, FeedStatus.ACTIVE))
                    .willReturn(Collections.emptyList());

            // when
            List<Feed> foundFeeds = feedFinder.findAllByWriterId(nonExistentWriterId, FeedStatus.ACTIVE);

            // then
            assertThat(foundFeeds).isEmpty();
        }

        @Test
        @DisplayName("삭제된 상태의 피드 찾기")
        void findAllByWriterIdWithDeletedStatus() {
            // given
            given(feedRepository.findAllByWriterIdAndStatus(WRITER_ID, FeedStatus.USER_DELETED))
                    .willReturn(Collections.emptyList());

            // when
            List<Feed> foundFeeds = feedFinder.findAllByWriterId(WRITER_ID, FeedStatus.USER_DELETED);

            // then
            assertThat(foundFeeds).isEmpty();
        }

        @Test
        @DisplayName("null 작성자 ID로 피드 찾기")
        void findAllByNullWriterId() {
            // given
            given(feedRepository.findAllByWriterIdAndStatus(null, FeedStatus.ACTIVE))
                    .willReturn(Collections.emptyList());

            // when
            List<Feed> foundFeeds = feedFinder.findAllByWriterId(null, FeedStatus.ACTIVE);

            // then
            assertThat(foundFeeds).isEmpty();
        }
    }

    @Nested
    @DisplayName("FeedFinder 조건으로 피드 찾기 테스트")
    class FindAllWithConditionTest {

        @Test
        @DisplayName("조건으로 피드 찾기 성공 (첫 페이지)")
        void findAllWithConditionFirstPageSuccess() {
            // given
            LocalDate date = LocalDate.of(2024, 1, 1);
            RequestFindFeedCondition condition = new RequestFindFeedCondition(WRITER_ID, date, FeedStatus.ACTIVE, 10L, null);
            LocalDateTime startDate = date.atStartOfDay();
            LocalDateTime endDate = date.atStartOfDay().plusDays(1).minusSeconds(1);
            
            given(feedRepository.findFeedsByConditions(WRITER_ID, FeedStatus.ACTIVE, startDate, endDate, PAGE_SIZE))
                    .willReturn(mockFeeds);

            // when
            List<Feed> foundFeeds = feedFinder.findAllWithCondition(condition, null, PAGE_SIZE);

            // then
            assertThat(foundFeeds).isEqualTo(mockFeeds);
            verify(feedRepository).findFeedsByConditions(WRITER_ID, FeedStatus.ACTIVE, startDate, endDate, PAGE_SIZE);
        }

        @Test
        @DisplayName("조건으로 피드 찾기 성공 (다음 페이지)")
        void findAllWithConditionNextPageSuccess() {
            // given
            LocalDate date = LocalDate.of(2024, 1, 1);
            RequestFindFeedCondition condition = new RequestFindFeedCondition(WRITER_ID, date, FeedStatus.ACTIVE, 10L, null);
            LocalDateTime startDate = date.atStartOfDay();
            LocalDateTime endDate = date.atStartOfDay().plusDays(1).minusSeconds(1);
            Long lastId = 5L;
            
            given(feedRepository.findFeedsByConditions(WRITER_ID, FeedStatus.ACTIVE, startDate, endDate, PAGE_SIZE, lastId))
                    .willReturn(mockFeeds);

            // when
            List<Feed> foundFeeds = feedFinder.findAllWithCondition(condition, lastId, PAGE_SIZE);

            // then
            assertThat(foundFeeds).isEqualTo(mockFeeds);
            verify(feedRepository).findFeedsByConditions(WRITER_ID, FeedStatus.ACTIVE, startDate, endDate, PAGE_SIZE, lastId);
        }

        @Test
        @DisplayName("날짜 없는 조건으로 피드 찾기")
        void findAllWithConditionWithoutDate() {
            // given
            RequestFindFeedCondition condition = new RequestFindFeedCondition(WRITER_ID, null, FeedStatus.ACTIVE, 10L, null);
            
            given(feedRepository.findFeedsByConditions(WRITER_ID, FeedStatus.ACTIVE, null, null, PAGE_SIZE))
                    .willReturn(mockFeeds);

            // when
            List<Feed> foundFeeds = feedFinder.findAllWithCondition(condition, null, PAGE_SIZE);

            // then
            assertThat(foundFeeds).isEqualTo(mockFeeds);
            verify(feedRepository).findFeedsByConditions(WRITER_ID, FeedStatus.ACTIVE, null, null, PAGE_SIZE);
        }

        @Test
        @DisplayName("빈 결과 조건으로 피드 찾기")
        void findAllWithConditionEmptyResult() {
            // given
            LocalDate date = LocalDate.of(2024, 1, 1);
            RequestFindFeedCondition condition = new RequestFindFeedCondition(999L, date, FeedStatus.ACTIVE, 10L, null);
            LocalDateTime startDate = date.atStartOfDay();
            LocalDateTime endDate = date.atStartOfDay().plusDays(1).minusSeconds(1);
            
            given(feedRepository.findFeedsByConditions(999L, FeedStatus.ACTIVE, startDate, endDate, PAGE_SIZE))
                    .willReturn(Collections.emptyList());

            // when
            List<Feed> foundFeeds = feedFinder.findAllWithCondition(condition, null, PAGE_SIZE);

            // then
            assertThat(foundFeeds).isEmpty();
        }

        @Test
        @DisplayName("모든 조건이 null인 경우")
        void findAllWithConditionAllNull() {
            // given
            RequestFindFeedCondition condition = new RequestFindFeedCondition(null, null, null, 10L, null);
            
            given(feedRepository.findFeedsByConditions(null, null, null, null, PAGE_SIZE))
                    .willReturn(Collections.emptyList());

            // when
            List<Feed> foundFeeds = feedFinder.findAllWithCondition(condition, null, PAGE_SIZE);

            // then
            assertThat(foundFeeds).isEmpty();
        }
    }

    @Nested
    @DisplayName("FeedFinder 팔로잉 사용자 피드 찾기 테스트")
    class FindByFollowingIdsTest {

        private List<Long> followingIds;

        @BeforeEach
        void setUp() {
            followingIds = Arrays.asList(1L, 2L, 3L);
        }

        @Test
        @DisplayName("팔로잉 사용자 피드 찾기 성공 (첫 페이지)")
        void findByFollowingIdsFirstPageSuccess() {
            // given
            given(feedRepository.findRecentFeedsByFollowing(followingIds, PAGE_SIZE))
                    .willReturn(mockFeeds);

            // when
            List<Feed> foundFeeds = feedFinder.findByFollowingIds(followingIds, null, PAGE_SIZE);

            // then
            assertThat(foundFeeds).isEqualTo(mockFeeds);
            verify(feedRepository).findRecentFeedsByFollowing(followingIds, PAGE_SIZE);
        }

        @Test
        @DisplayName("팔로잉 사용자 피드 찾기 성공 (다음 페이지)")
        void findByFollowingIdsNextPageSuccess() {
            // given
            Long lastId = 5L;
            given(feedRepository.findRecentFeedsByFollowing(followingIds, PAGE_SIZE, lastId))
                    .willReturn(mockFeeds);

            // when
            List<Feed> foundFeeds = feedFinder.findByFollowingIds(followingIds, lastId, PAGE_SIZE);

            // then
            assertThat(foundFeeds).isEqualTo(mockFeeds);
            verify(feedRepository).findRecentFeedsByFollowing(followingIds, PAGE_SIZE, lastId);
        }

        @Test
        @DisplayName("빈 팔로잉 목록으로 피드 찾기")
        void findByEmptyFollowingIds() {
            // given
            List<Long> emptyFollowingIds = Collections.emptyList();
            given(feedRepository.findRecentFeedsByFollowing(emptyFollowingIds, PAGE_SIZE))
                    .willReturn(Collections.emptyList());

            // when
            List<Feed> foundFeeds = feedFinder.findByFollowingIds(emptyFollowingIds, null, PAGE_SIZE);

            // then
            assertThat(foundFeeds).isEmpty();
        }

        @Test
        @DisplayName("null 팔로잉 목록으로 피드 찾기")
        void findByNullFollowingIds() {
            // given
            given(feedRepository.findRecentFeedsByFollowing(null, PAGE_SIZE))
                    .willReturn(Collections.emptyList());

            // when
            List<Feed> foundFeeds = feedFinder.findByFollowingIds(null, null, PAGE_SIZE);

            // then
            assertThat(foundFeeds).isEmpty();
        }

        @Test
        @DisplayName("단일 팔로잉 사용자 피드 찾기")
        void findBySingleFollowingId() {
            // given
            List<Long> singleFollowingId = Arrays.asList(1L);
            given(feedRepository.findRecentFeedsByFollowing(singleFollowingId, PAGE_SIZE))
                    .willReturn(Arrays.asList(mockFeed));

            // when
            List<Feed> foundFeeds = feedFinder.findByFollowingIds(singleFollowingId, null, PAGE_SIZE);

            // then
            assertThat(foundFeeds).hasSize(1);
            assertThat(foundFeeds.get(0)).isEqualTo(mockFeed);
        }
    }

    @Nested
    @DisplayName("FeedFinder 경계값 테스트")
    class BoundaryTest {

        @Test
        @DisplayName("최대값 ID로 피드 찾기")
        void findByMaxValueId() {
            // given
            Long maxId = Long.MAX_VALUE;
            given(feedRepository.findById(maxId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> feedFinder.findById(maxId))
                    .isInstanceOf(NotFoundFeedException.class);
        }

        @Test
        @DisplayName("최소값 페이지 사이즈로 피드 찾기")
        void findWithMinPageSize() {
            // given
            Long minPageSize = 1L;
            given(feedRepository.findRecentFeedsByFollowing(Arrays.asList(1L), minPageSize))
                    .willReturn(Arrays.asList(mockFeed));

            // when
            List<Feed> foundFeeds = feedFinder.findByFollowingIds(Arrays.asList(1L), null, minPageSize);

            // then
            assertThat(foundFeeds).hasSize(1);
        }

        @Test
        @DisplayName("큰 페이지 사이즈로 피드 찾기")
        void findWithLargePageSize() {
            // given
            Long largePageSize = 1000L;
            given(feedRepository.findRecentFeedsByFollowing(Arrays.asList(1L), largePageSize))
                    .willReturn(mockFeeds);

            // when
            List<Feed> foundFeeds = feedFinder.findByFollowingIds(Arrays.asList(1L), null, largePageSize);

            // then
            assertThat(foundFeeds).isEqualTo(mockFeeds);
        }
    }
}