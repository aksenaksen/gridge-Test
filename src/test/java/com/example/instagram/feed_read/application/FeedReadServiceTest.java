package com.example.instagram.feed_read.application;

import com.example.instagram.common.Page;
import com.example.instagram.feed.application.dto.out.ResponseFeedDto;
import com.example.instagram.feed.domain.FeedStatus;
import com.example.instagram.feed_read.client.FeedServiceClient;
import com.example.instagram.feed_read.infrastructor.FeedQueryModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedReadServiceTest {

    @Mock
    private SubscriptionClient subscriptionClient;

    @Mock
    private FeedServiceClient feedServiceClient;

    @Mock
    private CommentClient commentClient;

    @Mock
    private LikeClient likeClient;

    @InjectMocks
    private FeedReadService feedReadService;

    private final Long USER_ID = 1L;
    private final Long FEED_ID_1 = 1L;
    private final Long FEED_ID_2 = 2L;
    private final Long PAGE_SIZE = 10L;

    private Page mockPage;
    private List<Long> mockFollowingList;
    private List<ResponseFeedDto> mockFeeds;

    @BeforeEach
    void setUp() {
        mockPage = new Page(PAGE_SIZE, null);
        mockFollowingList = Arrays.asList(2L, 3L, 4L);
        
        LocalDateTime now = LocalDateTime.now();
        mockFeeds = Arrays.asList(
                new ResponseFeedDto(FEED_ID_1, 2L, "첫 번째 피드", FeedStatus.ACTIVE, Collections.emptyList(), 0, now, now),
                new ResponseFeedDto(FEED_ID_2, 3L, "두 번째 피드", FeedStatus.ACTIVE, Arrays.asList("image1.jpg", "image2.jpg"), 2, now, now)
        );
    }

    @Nested
    @DisplayName("FeedReadService 피드 가져오기 테스트")
    class FetchFeedsTest {

        @Test
        @DisplayName("피드 가져오기 성공")
        void fetchFeedsSuccess() {
            // given
            Long likeCount1 = 5L;
            Long likeCount2 = 10L;
            Long commentCount1 = 3L;
            Long commentCount2 = 7L;

            given(subscriptionClient.readSubscriptions(USER_ID)).willReturn(mockFollowingList);
            given(feedServiceClient.read(mockFollowingList, mockPage)).willReturn(mockFeeds);
            given(likeClient.count(FEED_ID_1)).willReturn(likeCount1);
            given(likeClient.count(FEED_ID_2)).willReturn(likeCount2);
            given(commentClient.getCount(FEED_ID_1)).willReturn(commentCount1);
            given(commentClient.getCount(FEED_ID_2)).willReturn(commentCount2);

            // when
            List<FeedQueryModel> results = feedReadService.fetch(USER_ID, mockPage);

            // then
            assertThat(results).hasSize(2);
            
            FeedQueryModel firstFeed = results.get(0);
            assertThat(firstFeed.getFeedId()).isEqualTo(FEED_ID_1);
            assertThat(firstFeed.getLikeCount()).isEqualTo(likeCount1);
            assertThat(firstFeed.getCommentCount()).isEqualTo(commentCount1);
            
            FeedQueryModel secondFeed = results.get(1);
            assertThat(secondFeed.getFeedId()).isEqualTo(FEED_ID_2);
            assertThat(secondFeed.getLikeCount()).isEqualTo(likeCount2);
            assertThat(secondFeed.getCommentCount()).isEqualTo(commentCount2);

            verify(subscriptionClient).readSubscriptions(USER_ID);
            verify(feedServiceClient).read(mockFollowingList, mockPage);
            verify(likeClient).count(FEED_ID_1);
            verify(likeClient).count(FEED_ID_2);
            verify(commentClient).getCount(FEED_ID_1);
            verify(commentClient).getCount(FEED_ID_2);
        }

        @Test
        @DisplayName("팔로잉이 없는 사용자의 피드 가져오기")
        void fetchFeedsWithNoFollowings() {
            // given
            List<Long> emptyFollowingList = Collections.emptyList();
            List<ResponseFeedDto> emptyFeeds = Collections.emptyList();

            given(subscriptionClient.readSubscriptions(USER_ID)).willReturn(emptyFollowingList);
            given(feedServiceClient.read(emptyFollowingList, mockPage)).willReturn(emptyFeeds);

            // when
            List<FeedQueryModel> results = feedReadService.fetch(USER_ID, mockPage);

            // then
            assertThat(results).isEmpty();
            verify(subscriptionClient).readSubscriptions(USER_ID);
            verify(feedServiceClient).read(emptyFollowingList, mockPage);
            verify(likeClient, never()).count(any());
            verify(commentClient, never()).getCount(any());
        }

        @Test
        @DisplayName("피드가 없는 경우")
        void fetchFeedsWithNoFeeds() {
            // given
            List<ResponseFeedDto> emptyFeeds = Collections.emptyList();

            given(subscriptionClient.readSubscriptions(USER_ID)).willReturn(mockFollowingList);
            given(feedServiceClient.read(mockFollowingList, mockPage)).willReturn(emptyFeeds);

            // when
            List<FeedQueryModel> results = feedReadService.fetch(USER_ID, mockPage);

            // then
            assertThat(results).isEmpty();
            verify(subscriptionClient).readSubscriptions(USER_ID);
            verify(feedServiceClient).read(mockFollowingList, mockPage);
            verify(likeClient, never()).count(any());
            verify(commentClient, never()).getCount(any());
        }

        @Test
        @DisplayName("좋아요 수가 0인 피드")
        void fetchFeedsWithZeroLikes() {
            // given
            Long zeroLikeCount = 0L;
            Long commentCount = 5L;

            given(subscriptionClient.readSubscriptions(USER_ID)).willReturn(mockFollowingList);
            given(feedServiceClient.read(mockFollowingList, mockPage)).willReturn(Arrays.asList(mockFeeds.get(0)));
            given(likeClient.count(FEED_ID_1)).willReturn(zeroLikeCount);
            given(commentClient.getCount(FEED_ID_1)).willReturn(commentCount);

            // when
            List<FeedQueryModel> results = feedReadService.fetch(USER_ID, mockPage);

            // then
            assertThat(results).hasSize(1);
            assertThat(results.get(0).getLikeCount()).isEqualTo(0L);
            assertThat(results.get(0).getCommentCount()).isEqualTo(commentCount);
        }

        @Test
        @DisplayName("댓글 수가 0인 피드")
        void fetchFeedsWithZeroComments() {
            // given
            Long likeCount = 10L;
            Long zeroCommentCount = 0L;

            given(subscriptionClient.readSubscriptions(USER_ID)).willReturn(mockFollowingList);
            given(feedServiceClient.read(mockFollowingList, mockPage)).willReturn(Arrays.asList(mockFeeds.get(0)));
            given(likeClient.count(FEED_ID_1)).willReturn(likeCount);
            given(commentClient.getCount(FEED_ID_1)).willReturn(zeroCommentCount);

            // when
            List<FeedQueryModel> results = feedReadService.fetch(USER_ID, mockPage);

            // then
            assertThat(results).hasSize(1);
            assertThat(results.get(0).getLikeCount()).isEqualTo(likeCount);
            assertThat(results.get(0).getCommentCount()).isEqualTo(0L);
        }

        @Test
        @DisplayName("단일 피드 가져오기")
        void fetchSingleFeed() {
            // given
            Long likeCount = 15L;
            Long commentCount = 8L;
            List<ResponseFeedDto> singleFeed = Arrays.asList(mockFeeds.get(0));

            given(subscriptionClient.readSubscriptions(USER_ID)).willReturn(mockFollowingList);
            given(feedServiceClient.read(mockFollowingList, mockPage)).willReturn(singleFeed);
            given(likeClient.count(FEED_ID_1)).willReturn(likeCount);
            given(commentClient.getCount(FEED_ID_1)).willReturn(commentCount);

            // when
            List<FeedQueryModel> results = feedReadService.fetch(USER_ID, mockPage);

            // then
            assertThat(results).hasSize(1);
            assertThat(results.get(0).getFeedId()).isEqualTo(FEED_ID_1);
            assertThat(results.get(0).getLikeCount()).isEqualTo(likeCount);
            assertThat(results.get(0).getCommentCount()).isEqualTo(commentCount);
        }
    }

    @Nested
    @DisplayName("FeedReadService 예외 처리 테스트")
    class ExceptionHandlingTest {

        @Test
        @DisplayName("SubscriptionClient에서 예외 발생")
        void subscriptionClientThrowsException() {
            // given
            given(subscriptionClient.readSubscriptions(USER_ID))
                    .willThrow(new RuntimeException("Subscription service error"));

            // when & then
            assertThatThrownBy(() -> feedReadService.fetch(USER_ID, mockPage))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Subscription service error");

            verify(subscriptionClient).readSubscriptions(USER_ID);
            verify(feedServiceClient, never()).read(any(), any());
        }

        @Test
        @DisplayName("FeedServiceClient에서 예외 발생")
        void feedServiceClientThrowsException() {
            // given
            given(subscriptionClient.readSubscriptions(USER_ID)).willReturn(mockFollowingList);
            given(feedServiceClient.read(mockFollowingList, mockPage))
                    .willThrow(new RuntimeException("Feed service error"));

            // when & then
            assertThatThrownBy(() -> feedReadService.fetch(USER_ID, mockPage))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Feed service error");

            verify(subscriptionClient).readSubscriptions(USER_ID);
            verify(feedServiceClient).read(mockFollowingList, mockPage);
        }

        @Test
        @DisplayName("LikeClient에서 예외 발생")
        void likeClientThrowsException() {
            // given
            given(subscriptionClient.readSubscriptions(USER_ID)).willReturn(mockFollowingList);
            given(feedServiceClient.read(mockFollowingList, mockPage)).willReturn(Arrays.asList(mockFeeds.get(0)));
            given(likeClient.count(FEED_ID_1)).willThrow(new RuntimeException("Like service error"));

            // when & then
            assertThatThrownBy(() -> feedReadService.fetch(USER_ID, mockPage))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Like service error");

            verify(likeClient).count(FEED_ID_1);
        }

        @Test
        @DisplayName("CommentClient에서 예외 발생")
        void commentClientThrowsException() {
            // given
            Long likeCount = 5L;
            
            given(subscriptionClient.readSubscriptions(USER_ID)).willReturn(mockFollowingList);
            given(feedServiceClient.read(mockFollowingList, mockPage)).willReturn(Arrays.asList(mockFeeds.get(0)));
            given(likeClient.count(FEED_ID_1)).willReturn(likeCount);
            given(commentClient.getCount(FEED_ID_1)).willThrow(new RuntimeException("Comment service error"));

            // when & then
            assertThatThrownBy(() -> feedReadService.fetch(USER_ID, mockPage))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Comment service error");

            verify(commentClient).getCount(FEED_ID_1);
        }

        @Test
        @DisplayName("첫 번째 피드 처리 중 예외 발생")
        void exceptionDuringFirstFeedProcessing() {
            // given
            given(subscriptionClient.readSubscriptions(USER_ID)).willReturn(mockFollowingList);
            given(feedServiceClient.read(mockFollowingList, mockPage)).willReturn(mockFeeds);
            given(likeClient.count(FEED_ID_1)).willThrow(new RuntimeException("First feed error"));

            // when & then
            assertThatThrownBy(() -> feedReadService.fetch(USER_ID, mockPage))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("First feed error");

            verify(likeClient).count(FEED_ID_1);
            verify(likeClient, never()).count(FEED_ID_2);
            verify(commentClient, never()).getCount(any());
        }

        @Test
        @DisplayName("두 번째 피드 처리 중 예외 발생")
        void exceptionDuringSecondFeedProcessing() {
            // given
            Long likeCount1 = 5L;
            Long commentCount1 = 3L;

            given(subscriptionClient.readSubscriptions(USER_ID)).willReturn(mockFollowingList);
            given(feedServiceClient.read(mockFollowingList, mockPage)).willReturn(mockFeeds);
            given(likeClient.count(FEED_ID_1)).willReturn(likeCount1);
            given(commentClient.getCount(FEED_ID_1)).willReturn(commentCount1);
            given(likeClient.count(FEED_ID_2)).willThrow(new RuntimeException("Second feed error"));

            // when & then
            assertThatThrownBy(() -> feedReadService.fetch(USER_ID, mockPage))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Second feed error");

            verify(likeClient).count(FEED_ID_1);
            verify(commentClient).getCount(FEED_ID_1);
            verify(likeClient).count(FEED_ID_2);
            verify(commentClient, never()).getCount(FEED_ID_2);
        }
    }

    @Nested
    @DisplayName("FeedReadService 경계값 테스트")
    class BoundaryTest {

        @Test
        @DisplayName("null 사용자 ID로 피드 가져오기")
        void fetchFeedsWithNullUserId() {
            // given
            given(subscriptionClient.readSubscriptions(null))
                    .willThrow(new RuntimeException("Invalid user ID"));

            // when & then
            assertThatThrownBy(() -> feedReadService.fetch(null, mockPage))
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("null 페이지로 피드 가져오기")
        void fetchFeedsWithNullPage() {
            // given
            given(subscriptionClient.readSubscriptions(USER_ID)).willReturn(mockFollowingList);
            given(feedServiceClient.read(mockFollowingList, null)).willReturn(mockFeeds);
            given(likeClient.count(any())).willReturn(0L);
            given(commentClient.getCount(any())).willReturn(0L);

            // when
            List<FeedQueryModel> results = feedReadService.fetch(USER_ID, null);

            // then
            assertThat(results).hasSize(2);
            verify(feedServiceClient).read(mockFollowingList, null);
        }

        @Test
        @DisplayName("0 사용자 ID로 피드 가져오기")
        void fetchFeedsWithZeroUserId() {
            // given
            given(subscriptionClient.readSubscriptions(0L)).willReturn(Collections.emptyList());
            given(feedServiceClient.read(Collections.emptyList(), mockPage)).willReturn(Collections.emptyList());

            // when
            List<FeedQueryModel> results = feedReadService.fetch(0L, mockPage);

            // then
            assertThat(results).isEmpty();
        }

        @Test
        @DisplayName("음수 사용자 ID로 피드 가져오기")
        void fetchFeedsWithNegativeUserId() {
            // given
            given(subscriptionClient.readSubscriptions(-1L)).willReturn(Collections.emptyList());
            given(feedServiceClient.read(Collections.emptyList(), mockPage)).willReturn(Collections.emptyList());

            // when
            List<FeedQueryModel> results = feedReadService.fetch(-1L, mockPage);

            // then
            assertThat(results).isEmpty();
        }

        @Test
        @DisplayName("매우 큰 사용자 ID로 피드 가져오기")
        void fetchFeedsWithLargeUserId() {
            // given
            Long largeUserId = Long.MAX_VALUE;
            given(subscriptionClient.readSubscriptions(largeUserId)).willReturn(Collections.emptyList());
            given(feedServiceClient.read(Collections.emptyList(), mockPage)).willReturn(Collections.emptyList());

            // when
            List<FeedQueryModel> results = feedReadService.fetch(largeUserId, mockPage);

            // then
            assertThat(results).isEmpty();
        }

        @Test
        @DisplayName("매우 많은 팔로잉이 있는 사용자")
        void fetchFeedsWithManyFollowings() {
            // given
            List<Long> manyFollowings = Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);
            
            given(subscriptionClient.readSubscriptions(USER_ID)).willReturn(manyFollowings);
            given(feedServiceClient.read(manyFollowings, mockPage)).willReturn(mockFeeds);
            given(likeClient.count(any())).willReturn(5L);
            given(commentClient.getCount(any())).willReturn(3L);

            // when
            List<FeedQueryModel> results = feedReadService.fetch(USER_ID, mockPage);

            // then
            assertThat(results).hasSize(2);
            verify(subscriptionClient).readSubscriptions(USER_ID);
            verify(feedServiceClient).read(manyFollowings, mockPage);
        }

        @Test
        @DisplayName("매우 큰 좋아요 수와 댓글 수")
        void fetchFeedsWithLargeCounts() {
            // given
            Long largeLikeCount = Long.MAX_VALUE;
            Long largeCommentCount = Long.MAX_VALUE;

            given(subscriptionClient.readSubscriptions(USER_ID)).willReturn(mockFollowingList);
            given(feedServiceClient.read(mockFollowingList, mockPage)).willReturn(Arrays.asList(mockFeeds.get(0)));
            given(likeClient.count(FEED_ID_1)).willReturn(largeLikeCount);
            given(commentClient.getCount(FEED_ID_1)).willReturn(largeCommentCount);

            // when
            List<FeedQueryModel> results = feedReadService.fetch(USER_ID, mockPage);

            // then
            assertThat(results).hasSize(1);
            assertThat(results.get(0).getLikeCount()).isEqualTo(largeLikeCount);
            assertThat(results.get(0).getCommentCount()).isEqualTo(largeCommentCount);
        }
    }

    @Nested
    @DisplayName("FeedReadService 성능 테스트")
    class PerformanceTest {

        @Test
        @DisplayName("많은 피드를 처리하는 경우")
        void fetchManyFeeds() {
            // given
            LocalDateTime now = LocalDateTime.now();
            List<ResponseFeedDto> manyFeeds = Arrays.asList(
                    new ResponseFeedDto(1L, 2L, "피드1", FeedStatus.ACTIVE, Collections.emptyList(), 0, now, now),
                    new ResponseFeedDto(2L, 3L, "피드2", FeedStatus.ACTIVE, Collections.emptyList(), 0, now, now),
                    new ResponseFeedDto(3L, 4L, "피드3", FeedStatus.ACTIVE, Collections.emptyList(), 0, now, now),
                    new ResponseFeedDto(4L, 5L, "피드4", FeedStatus.ACTIVE, Collections.emptyList(), 0, now, now),
                    new ResponseFeedDto(5L, 6L, "피드5", FeedStatus.ACTIVE, Collections.emptyList(), 0, now, now)
            );

            given(subscriptionClient.readSubscriptions(USER_ID)).willReturn(mockFollowingList);
            given(feedServiceClient.read(mockFollowingList, mockPage)).willReturn(manyFeeds);
            given(likeClient.count(any())).willReturn(10L);
            given(commentClient.getCount(any())).willReturn(5L);

            // when
            List<FeedQueryModel> results = feedReadService.fetch(USER_ID, mockPage);

            // then
            assertThat(results).hasSize(5);
            verify(likeClient, times(5)).count(any());
            verify(commentClient, times(5)).getCount(any());
        }

        @Test
        @DisplayName("빠른 연속 호출")
        void rapidConsecutiveCalls() {
            // given
            given(subscriptionClient.readSubscriptions(USER_ID)).willReturn(mockFollowingList);
            given(feedServiceClient.read(mockFollowingList, mockPage)).willReturn(mockFeeds);
            given(likeClient.count(any())).willReturn(5L);
            given(commentClient.getCount(any())).willReturn(3L);

            // when
            List<FeedQueryModel> results1 = feedReadService.fetch(USER_ID, mockPage);
            List<FeedQueryModel> results2 = feedReadService.fetch(USER_ID, mockPage);
            List<FeedQueryModel> results3 = feedReadService.fetch(USER_ID, mockPage);

            // then
            assertThat(results1).hasSize(2);
            assertThat(results2).hasSize(2);
            assertThat(results3).hasSize(2);
            
            verify(subscriptionClient, times(3)).readSubscriptions(USER_ID);
            verify(feedServiceClient, times(3)).read(mockFollowingList, mockPage);
        }
    }

    @Nested
    @DisplayName("FeedReadService 통합 시나리오 테스트")
    class IntegrationScenarioTest {

        @Test
        @DisplayName("완전한 피드 읽기 플로우")
        void completeReadFlow() {
            // given
            Long likeCount1 = 12L;
            Long likeCount2 = 8L;
            Long commentCount1 = 4L;
            Long commentCount2 = 6L;

            given(subscriptionClient.readSubscriptions(USER_ID)).willReturn(mockFollowingList);
            given(feedServiceClient.read(mockFollowingList, mockPage)).willReturn(mockFeeds);
            given(likeClient.count(FEED_ID_1)).willReturn(likeCount1);
            given(likeClient.count(FEED_ID_2)).willReturn(likeCount2);
            given(commentClient.getCount(FEED_ID_1)).willReturn(commentCount1);
            given(commentClient.getCount(FEED_ID_2)).willReturn(commentCount2);

            // when
            List<FeedQueryModel> results = feedReadService.fetch(USER_ID, mockPage);

            // then
            assertThat(results).hasSize(2);
            
            // 첫 번째 피드 검증
            FeedQueryModel firstFeed = results.get(0);
            assertThat(firstFeed.getFeedId()).isEqualTo(FEED_ID_1);
            assertThat(firstFeed.getUserId()).isEqualTo(2L);
            assertThat(firstFeed.getContent()).isEqualTo("첫 번째 피드");
            assertThat(firstFeed.getLikeCount()).isEqualTo(likeCount1);
            assertThat(firstFeed.getCommentCount()).isEqualTo(commentCount1);
            assertThat(firstFeed.getImageUrls()).isEmpty();

            // 두 번째 피드 검증
            FeedQueryModel secondFeed = results.get(1);
            assertThat(secondFeed.getFeedId()).isEqualTo(FEED_ID_2);
            assertThat(secondFeed.getUserId()).isEqualTo(3L);
            assertThat(secondFeed.getContent()).isEqualTo("두 번째 피드");
            assertThat(secondFeed.getLikeCount()).isEqualTo(likeCount2);
            assertThat(secondFeed.getCommentCount()).isEqualTo(commentCount2);
            assertThat(secondFeed.getImageUrls()).containsExactly("image1.jpg", "image2.jpg");

            // 모든 클라이언트 호출 검증
            verify(subscriptionClient).readSubscriptions(USER_ID);
            verify(feedServiceClient).read(mockFollowingList, mockPage);
            verify(likeClient).count(FEED_ID_1);
            verify(likeClient).count(FEED_ID_2);
            verify(commentClient).getCount(FEED_ID_1);
            verify(commentClient).getCount(FEED_ID_2);
        }

        @Test
        @DisplayName("새로운 사용자의 첫 피드 읽기")
        void newUserFirstFeedRead() {
            // given
            Long newUserId = 100L;
            given(subscriptionClient.readSubscriptions(newUserId)).willReturn(Collections.emptyList());
            given(feedServiceClient.read(Collections.emptyList(), mockPage)).willReturn(Collections.emptyList());

            // when
            List<FeedQueryModel> results = feedReadService.fetch(newUserId, mockPage);

            // then
            assertThat(results).isEmpty();
            verify(subscriptionClient).readSubscriptions(newUserId);
            verify(feedServiceClient).read(Collections.emptyList(), mockPage);
            verifyNoInteractions(likeClient);
            verifyNoInteractions(commentClient);
        }

        @Test
        @DisplayName("팔로잉은 있지만 피드가 없는 경우")
        void followingsExistButNoFeeds() {
            // given
            given(subscriptionClient.readSubscriptions(USER_ID)).willReturn(mockFollowingList);
            given(feedServiceClient.read(mockFollowingList, mockPage)).willReturn(Collections.emptyList());

            // when
            List<FeedQueryModel> results = feedReadService.fetch(USER_ID, mockPage);

            // then
            assertThat(results).isEmpty();
            verify(subscriptionClient).readSubscriptions(USER_ID);
            verify(feedServiceClient).read(mockFollowingList, mockPage);
            verifyNoInteractions(likeClient);
            verifyNoInteractions(commentClient);
        }
    }
}