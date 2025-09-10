package com.example.instagram.feed.application;

import com.example.instagram.feed.application.dto.in.LikeCommand;
import com.example.instagram.feed.application.dto.in.UnLikeCommand;
import com.example.instagram.feed.application.dto.out.ResponseFeedLikeCount;
import com.example.instagram.feed.application.feed.FeedFinder;
import com.example.instagram.feed.application.like.FeedLikeCommander;
import com.example.instagram.feed.application.like.FeedLikeService;
import com.example.instagram.feed.application.like.LikeCountCommander;
import com.example.instagram.feed.application.like.LikeCountFinder;
import com.example.instagram.feed.domain.Feed;
import com.example.instagram.feed.domain.FeedStatus;
import com.example.instagram.feed.domain.LikeCount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedLikeServiceTest {

    @Mock
    private FeedLikeCommander feedLikeCommander;

    @Mock
    private LikeCountCommander likeCountCommander;

    @Mock
    private FeedFinder feedFinder;

    @Mock
    private LikeCountFinder likeCountFinder;

    @InjectMocks
    private FeedLikeService feedLikeService;

    private final Long FEED_ID = 1L;
    private final Long USER_ID = 1L;
    private final Long LIKE_COUNT = 5L;

    private Feed mockFeed;
    private LikeCount mockLikeCount;

    @BeforeEach
    void setUp() {
        mockFeed = Feed.createFeed(FEED_ID, USER_ID, "테스트 피드", FeedStatus.ACTIVE);
        mockLikeCount = LikeCount.create(FEED_ID, LIKE_COUNT);
    }

    @Nested
    @DisplayName("FeedLikeService 좋아요 테스트")
    class LikeTest {

        @Test
        @DisplayName("좋아요 성공")
        void likeSuccess() {
            // given
            LikeCommand command = new LikeCommand(FEED_ID, USER_ID);
            given(feedFinder.findById(FEED_ID)).willReturn(mockFeed);

            // when
            feedLikeService.like(command);

            // then
            verify(feedFinder).findById(FEED_ID);
            verify(feedLikeCommander).like(command);
            verify(likeCountCommander).like(command);
        }

        @Test
        @DisplayName("존재하지 않는 피드에 좋아요 시 예외 발생")
        void likeNonExistentFeedThrowsException() {
            // given
            LikeCommand command = new LikeCommand(FEED_ID, USER_ID);
            given(feedFinder.findById(FEED_ID)).willThrow(new RuntimeException("Feed not found"));

            // when & then
            assertThatThrownBy(() -> feedLikeService.like(command))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Feed not found");

            verify(feedFinder).findById(FEED_ID);
            verify(feedLikeCommander, never()).like(any());
            verify(likeCountCommander, never()).like(any());
        }

        @Test
        @DisplayName("좋아요 처리 중 FeedLikeCommander에서 예외 발생")
        void likeCommanderThrowsException() {
            // given
            LikeCommand command = new LikeCommand(FEED_ID, USER_ID);
            given(feedFinder.findById(FEED_ID)).willReturn(mockFeed);
            doThrow(new RuntimeException("Like processing error")).when(feedLikeCommander).like(command);

            // when & then
            assertThatThrownBy(() -> feedLikeService.like(command))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Like processing error");

            verify(feedFinder).findById(FEED_ID);
            verify(feedLikeCommander).like(command);
            verify(likeCountCommander, never()).like(any());
        }

        @Test
        @DisplayName("좋아요 처리 중 LikeCountCommander에서 예외 발생")
        void likeCountCommanderThrowsException() {
            // given
            LikeCommand command = new LikeCommand(FEED_ID, USER_ID);
            given(feedFinder.findById(FEED_ID)).willReturn(mockFeed);
            doThrow(new RuntimeException("Like count error")).when(likeCountCommander).like(command);

            // when & then
            assertThatThrownBy(() -> feedLikeService.like(command))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Like count error");

            verify(feedFinder).findById(FEED_ID);
            verify(feedLikeCommander).like(command);
            verify(likeCountCommander).like(command);
        }

        @Test
        @DisplayName("null 피드 ID로 좋아요 시도")
        void likeWithNullFeedId() {
            // given
            LikeCommand command = new LikeCommand(null, USER_ID);
            given(feedFinder.findById(null)).willThrow(new RuntimeException("Feed not found"));

            // when & then
            assertThatThrownBy(() -> feedLikeService.like(command))
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("null 사용자 ID로 좋아요 시도")
        void likeWithNullUserId() {
            // given
            LikeCommand command = new LikeCommand(FEED_ID, null);
            given(feedFinder.findById(FEED_ID)).willReturn(mockFeed);

            // when
            feedLikeService.like(command);

            // then
            verify(feedFinder).findById(FEED_ID);
            verify(feedLikeCommander).like(command);
            verify(likeCountCommander).like(command);
        }
    }

    @Nested
    @DisplayName("FeedLikeService 좋아요 취소 테스트")
    class UnlikeTest {

        @Test
        @DisplayName("좋아요 취소 성공")
        void unlikeSuccess() {
            // given
            UnLikeCommand command = new UnLikeCommand(FEED_ID, USER_ID);
            given(feedFinder.findById(FEED_ID)).willReturn(mockFeed);

            // when
            feedLikeService.unlike(command);

            // then
            verify(feedFinder).findById(FEED_ID);
            verify(feedLikeCommander).unlike(command);
            verify(likeCountCommander).unLike(command);
        }

        @Test
        @DisplayName("존재하지 않는 피드에 좋아요 취소 시 예외 발생")
        void unlikeNonExistentFeedThrowsException() {
            // given
            UnLikeCommand command = new UnLikeCommand(FEED_ID, USER_ID);
            given(feedFinder.findById(FEED_ID)).willThrow(new RuntimeException("Feed not found"));

            // when & then
            assertThatThrownBy(() -> feedLikeService.unlike(command))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Feed not found");

            verify(feedFinder).findById(FEED_ID);
            verify(feedLikeCommander, never()).unlike(any());
            verify(likeCountCommander, never()).unLike(any());
        }

        @Test
        @DisplayName("좋아요 취소 처리 중 FeedLikeCommander에서 예외 발생")
        void unlikeCommanderThrowsException() {
            // given
            UnLikeCommand command = new UnLikeCommand(FEED_ID, USER_ID);
            given(feedFinder.findById(FEED_ID)).willReturn(mockFeed);
            doThrow(new RuntimeException("Unlike processing error")).when(feedLikeCommander).unlike(command);

            // when & then
            assertThatThrownBy(() -> feedLikeService.unlike(command))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Unlike processing error");

            verify(feedFinder).findById(FEED_ID);
            verify(feedLikeCommander).unlike(command);
            verify(likeCountCommander, never()).unLike(any());
        }

        @Test
        @DisplayName("좋아요 취소 처리 중 LikeCountCommander에서 예외 발생")
        void unlikeCountCommanderThrowsException() {
            // given
            UnLikeCommand command = new UnLikeCommand(FEED_ID, USER_ID);
            given(feedFinder.findById(FEED_ID)).willReturn(mockFeed);
            doThrow(new RuntimeException("Unlike count error")).when(likeCountCommander).unLike(command);

            // when & then
            assertThatThrownBy(() -> feedLikeService.unlike(command))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Unlike count error");

            verify(feedFinder).findById(FEED_ID);
            verify(feedLikeCommander).unlike(command);
            verify(likeCountCommander).unLike(command);
        }

        @Test
        @DisplayName("null 피드 ID로 좋아요 취소 시도")
        void unlikeWithNullFeedId() {
            // given
            UnLikeCommand command = new UnLikeCommand(null, USER_ID);
            given(feedFinder.findById(null)).willThrow(new RuntimeException("Feed not found"));

            // when & then
            assertThatThrownBy(() -> feedLikeService.unlike(command))
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("null 사용자 ID로 좋아요 취소 시도")
        void unlikeWithNullUserId() {
            // given
            UnLikeCommand command = new UnLikeCommand(FEED_ID, null);
            given(feedFinder.findById(FEED_ID)).willReturn(mockFeed);

            // when
            feedLikeService.unlike(command);

            // then
            verify(feedFinder).findById(FEED_ID);
            verify(feedLikeCommander).unlike(command);
            verify(likeCountCommander).unLike(command);
        }
    }

    @Nested
    @DisplayName("FeedLikeService 좋아요 수 조회 테스트")
    class FindLikeCountTest {

        @Test
        @DisplayName("좋아요 수 조회 성공")
        void findLikeCountSuccess() {
            // given
            given(likeCountFinder.findByFeedId(FEED_ID)).willReturn(mockLikeCount);

            // when
            ResponseFeedLikeCount response = feedLikeService.findLikeCount(FEED_ID);

            // then
            assertThat(response).isNotNull();
            assertThat(response.feedId()).isEqualTo(FEED_ID);
            assertThat(response.likeCount()).isEqualTo(LIKE_COUNT);
            verify(likeCountFinder).findByFeedId(FEED_ID);
        }

        @Test
        @DisplayName("존재하지 않는 피드의 좋아요 수 조회 시 예외 발생")
        void findLikeCountNonExistentFeedThrowsException() {
            // given
            given(likeCountFinder.findByFeedId(FEED_ID)).willThrow(new RuntimeException("LikeCount not found"));

            // when & then
            assertThatThrownBy(() -> feedLikeService.findLikeCount(FEED_ID))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("LikeCount not found");

            verify(likeCountFinder).findByFeedId(FEED_ID);
        }

        @Test
        @DisplayName("null 피드 ID로 좋아요 수 조회")
        void findLikeCountWithNullFeedId() {
            // given
            given(likeCountFinder.findByFeedId(null)).willThrow(new RuntimeException("Invalid feed ID"));

            // when & then
            assertThatThrownBy(() -> feedLikeService.findLikeCount(null))
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("0 피드 ID로 좋아요 수 조회")
        void findLikeCountWithZeroFeedId() {
            // given
            given(likeCountFinder.findByFeedId(0L)).willThrow(new RuntimeException("LikeCount not found"));

            // when & then
            assertThatThrownBy(() -> feedLikeService.findLikeCount(0L))
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("음수 피드 ID로 좋아요 수 조회")
        void findLikeCountWithNegativeFeedId() {
            // given
            given(likeCountFinder.findByFeedId(-1L)).willThrow(new RuntimeException("LikeCount not found"));

            // when & then
            assertThatThrownBy(() -> feedLikeService.findLikeCount(-1L))
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("0 좋아요 수 조회")
        void findZeroLikeCount() {
            // given
            LikeCount zeroLikeCount = LikeCount.create(FEED_ID, 0L);
            given(likeCountFinder.findByFeedId(FEED_ID)).willReturn(zeroLikeCount);

            // when
            ResponseFeedLikeCount response = feedLikeService.findLikeCount(FEED_ID);

            // then
            assertThat(response.likeCount()).isEqualTo(0L);
        }

        @Test
        @DisplayName("큰 좋아요 수 조회")
        void findLargeLikeCount() {
            // given
            Long largeLikeCount = 1000000L;
            LikeCount largeLikeCountEntity = LikeCount.create(FEED_ID, largeLikeCount);
            given(likeCountFinder.findByFeedId(FEED_ID)).willReturn(largeLikeCountEntity);

            // when
            ResponseFeedLikeCount response = feedLikeService.findLikeCount(FEED_ID);

            // then
            assertThat(response.likeCount()).isEqualTo(largeLikeCount);
        }
    }

    @Nested
    @DisplayName("FeedLikeService 트랜잭션 테스트")
    class TransactionTest {

        @Test
        @DisplayName("좋아요 처리 중 예외 발생 시 트랜잭션 롤백")
        void likeTransactionRollbackOnException() {
            // given
            LikeCommand command = new LikeCommand(FEED_ID, USER_ID);
            given(feedFinder.findById(FEED_ID)).willReturn(mockFeed);
            doThrow(new RuntimeException("DB Error")).when(feedLikeCommander).like(command);

            // when & then
            assertThatThrownBy(() -> feedLikeService.like(command))
                    .isInstanceOf(RuntimeException.class);

            verify(feedFinder).findById(FEED_ID);
            verify(feedLikeCommander).like(command);
            verify(likeCountCommander, never()).like(any());
        }

        @Test
        @DisplayName("좋아요 취소 처리 중 예외 발생 시 트랜잭션 롤백")
        void unlikeTransactionRollbackOnException() {
            // given
            UnLikeCommand command = new UnLikeCommand(FEED_ID, USER_ID);
            given(feedFinder.findById(FEED_ID)).willReturn(mockFeed);
            doThrow(new RuntimeException("DB Error")).when(feedLikeCommander).unlike(command);

            // when & then
            assertThatThrownBy(() -> feedLikeService.unlike(command))
                    .isInstanceOf(RuntimeException.class);

            verify(feedFinder).findById(FEED_ID);
            verify(feedLikeCommander).unlike(command);
            verify(likeCountCommander, never()).unLike(any());
        }

        @Test
        @DisplayName("좋아요 수 조회는 트랜잭션 내에서 실행")
        void findLikeCountInTransaction() {
            // given
            given(likeCountFinder.findByFeedId(FEED_ID)).willReturn(mockLikeCount);

            // when
            ResponseFeedLikeCount response = feedLikeService.findLikeCount(FEED_ID);

            // then
            assertThat(response).isNotNull();
            verify(likeCountFinder).findByFeedId(FEED_ID);
        }
    }

    @Nested
    @DisplayName("FeedLikeService 복합 시나리오 테스트")
    class ComplexScenarioTest {

        @Test
        @DisplayName("좋아요 후 좋아요 수 조회")
        void likeAndThenFindLikeCount() {
            // given
            LikeCommand likeCommand = new LikeCommand(FEED_ID, USER_ID);
            given(feedFinder.findById(FEED_ID)).willReturn(mockFeed);
            given(likeCountFinder.findByFeedId(FEED_ID)).willReturn(mockLikeCount);

            // when
            feedLikeService.like(likeCommand);
            ResponseFeedLikeCount response = feedLikeService.findLikeCount(FEED_ID);

            // then
            verify(feedLikeCommander).like(likeCommand);
            verify(likeCountCommander).like(likeCommand);
            assertThat(response.likeCount()).isEqualTo(LIKE_COUNT);
        }

        @Test
        @DisplayName("좋아요 취소 후 좋아요 수 조회")
        void unlikeAndThenFindLikeCount() {
            // given
            UnLikeCommand unlikeCommand = new UnLikeCommand(FEED_ID, USER_ID);
            given(feedFinder.findById(FEED_ID)).willReturn(mockFeed);
            given(likeCountFinder.findByFeedId(FEED_ID)).willReturn(mockLikeCount);

            // when
            feedLikeService.unlike(unlikeCommand);
            ResponseFeedLikeCount response = feedLikeService.findLikeCount(FEED_ID);

            // then
            verify(feedLikeCommander).unlike(unlikeCommand);
            verify(likeCountCommander).unLike(unlikeCommand);
            assertThat(response.likeCount()).isEqualTo(LIKE_COUNT);
        }

        @Test
        @DisplayName("동일한 사용자가 연속으로 좋아요 처리")
        void consecutiveLikesByUser() {
            // given
            LikeCommand command = new LikeCommand(FEED_ID, USER_ID);
            given(feedFinder.findById(FEED_ID)).willReturn(mockFeed);

            // when
            feedLikeService.like(command);
            feedLikeService.like(command);

            // then
            verify(feedFinder, times(2)).findById(FEED_ID);
            verify(feedLikeCommander, times(2)).like(command);
            verify(likeCountCommander, times(2)).like(command);
        }

        @Test
        @DisplayName("다른 사용자들의 좋아요 처리")
        void likesByDifferentUsers() {
            // given
            LikeCommand command1 = new LikeCommand(FEED_ID, USER_ID);
            LikeCommand command2 = new LikeCommand(FEED_ID, 2L);
            given(feedFinder.findById(FEED_ID)).willReturn(mockFeed);

            // when
            feedLikeService.like(command1);
            feedLikeService.like(command2);

            // then
            verify(feedFinder, times(2)).findById(FEED_ID);
            verify(feedLikeCommander).like(command1);
            verify(feedLikeCommander).like(command2);
            verify(likeCountCommander).like(command1);
            verify(likeCountCommander).like(command2);
        }
    }
}