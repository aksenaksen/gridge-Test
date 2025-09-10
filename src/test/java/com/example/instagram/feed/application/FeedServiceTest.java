package com.example.instagram.feed.application;

import com.example.instagram.common.Page;
import com.example.instagram.feed.application.dto.in.FeedCreateCommand;
import com.example.instagram.feed.application.dto.in.FeedDeleteByAdminCommand;
import com.example.instagram.feed.application.dto.in.FeedDeleteCommand;
import com.example.instagram.feed.application.dto.in.FeedUpdateCommand;
import com.example.instagram.feed.application.dto.out.ResponseFeedDetail;
import com.example.instagram.feed.application.dto.out.ResponseFeedDto;
import com.example.instagram.feed.application.feed.FeedCommander;
import com.example.instagram.feed.application.feed.FeedFinder;
import com.example.instagram.feed.application.feed.FeedService;
import com.example.instagram.feed.domain.Feed;
import com.example.instagram.feed.domain.FeedStatus;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FeedServiceTest {

    @Mock
    private FeedFinder feedFinder;

    @Mock
    private FeedCommander feedCommander;

    @InjectMocks
    private FeedService feedService;

    private final Long FEED_ID = 1L;
    private final Long WRITER_ID = 1L;
    private final Long USER_ID = 1L;
    private final String CONTENT = "테스트 피드 내용입니다.";
    private final List<String> IMAGE_URLS = List.of("image1.jpg", "image2.jpg");

    private Feed mockFeed;
    private List<Feed> mockFeeds;
    private Page mockPage;

    @BeforeEach
    void setUp() {
        mockFeed = Feed.createFeed(FEED_ID, WRITER_ID, CONTENT, FeedStatus.ACTIVE);
        mockFeeds = Arrays.asList(
                mockFeed,
                Feed.createFeed(2L, WRITER_ID, "두 번째 피드", FeedStatus.ACTIVE)
        );
        mockPage = new Page(10L, null);
    }

    @Nested
    @DisplayName("FeedService 피드 생성 테스트")
    class CreateFeedTest {

        @Test
        @DisplayName("피드 생성 성공")
        void createFeedSuccess() {
            // given
            FeedCreateCommand command = new FeedCreateCommand(WRITER_ID, CONTENT, IMAGE_URLS);

            // when
            feedService.createFeed(command);

            // then
            verify(feedCommander).createFeed(command);
        }

        @Test
        @DisplayName("이미지 없이 피드 생성")
        void createFeedWithoutImages() {
            // given
            FeedCreateCommand command = new FeedCreateCommand(WRITER_ID, CONTENT, Collections.emptyList());

            // when
            feedService.createFeed(command);

            // then
            verify(feedCommander).createFeed(command);
        }

        @Test
        @DisplayName("null 내용으로 피드 생성")
        void createFeedWithNullContent() {
            // given
            FeedCreateCommand command = new FeedCreateCommand(WRITER_ID, null, IMAGE_URLS);

            // when
            feedService.createFeed(command);

            // then
            verify(feedCommander).createFeed(command);
        }
    }

    @Nested
    @DisplayName("FeedService 피드 조회 테스트")
    class GetFeedTest {

        @Test
        @DisplayName("ID로 피드 상세 조회 성공")
        void getFeedByIdSuccess() {
            // given
            given(feedFinder.findById(FEED_ID)).willReturn(mockFeed);

            // when
            ResponseFeedDetail response = feedService.getFeedById(FEED_ID);

            // then
            assertThat(response).isNotNull();
            assertThat(response.feedId()).isEqualTo(FEED_ID);
            assertThat(response.content()).isEqualTo(CONTENT);
            verify(feedFinder).findById(FEED_ID);
        }

        @Test
        @DisplayName("존재하지 않는 피드 조회 시 예외가 finder에서 발생")
        void getFeedByIdNotFound() {
            // given
            given(feedFinder.findById(FEED_ID)).willThrow(new RuntimeException("Feed not found"));

            // when & then
            assertThatThrownBy(() -> feedService.getFeedById(FEED_ID))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Feed not found");
        }
    }

    @Nested
    @DisplayName("FeedService 작성자별 피드 목록 조회 테스트")
    class GetFeedsByWriterTest {

        @Test
        @DisplayName("작성자 ID로 활성 피드 목록 조회 성공")
        void getAllActiveFeedsWithWriterIdSuccess() {
            // given
            given(feedFinder.findAllByWriterId(WRITER_ID, FeedStatus.ACTIVE)).willReturn(mockFeeds);

            // when
            List<ResponseFeedDto> responses = feedService.getAllActiveFeedsWithWriterId(WRITER_ID, FeedStatus.ACTIVE);

            // then
            assertThat(responses).hasSize(2);
            assertThat(responses.get(0).feedId()).isEqualTo(FEED_ID);
            assertThat(responses.get(0).content()).isEqualTo(CONTENT);
            verify(feedFinder).findAllByWriterId(WRITER_ID, FeedStatus.ACTIVE);
        }

        @Test
        @DisplayName("작성자 ID로 삭제된 피드 목록 조회")
        void getAllDeletedFeedsWithWriterId() {
            // given
            given(feedFinder.findAllByWriterId(WRITER_ID, FeedStatus.USER_DELETED))
                    .willReturn(Collections.emptyList());

            // when
            List<ResponseFeedDto> responses = feedService.getAllActiveFeedsWithWriterId(WRITER_ID, FeedStatus.USER_DELETED);

            // then
            assertThat(responses).isEmpty();
            verify(feedFinder).findAllByWriterId(WRITER_ID, FeedStatus.USER_DELETED);
        }

        @Test
        @DisplayName("존재하지 않는 작성자 ID로 피드 조회 시 빈 목록 반환")
        void getAllActiveFeedsWithNonExistentWriter() {
            // given
            Long nonExistentWriterId = 999L;
            given(feedFinder.findAllByWriterId(nonExistentWriterId, FeedStatus.ACTIVE))
                    .willReturn(Collections.emptyList());

            // when
            List<ResponseFeedDto> responses = feedService.getAllActiveFeedsWithWriterId(nonExistentWriterId, FeedStatus.ACTIVE);

            // then
            assertThat(responses).isEmpty();
        }
    }

    @Nested
    @DisplayName("FeedService 조건별 피드 목록 조회 테스트")
    class GetFeedsByConditionTest {

        @Test
        @DisplayName("조건으로 피드 목록 조회 성공 (페이지 있음)")
        void getAllFeedWithConditionSuccess() {
            // given
            LocalDate date = LocalDate.of(2024, 1, 1);
            RequestFindFeedCondition condition = new RequestFindFeedCondition(WRITER_ID, date, FeedStatus.ACTIVE, 10L, null);
            given(feedFinder.findAllWithCondition(condition, mockPage.lastId(), mockPage.pageSize()))
                    .willReturn(mockFeeds);

            // when
            List<ResponseFeedDto> responses = feedService.getAllFeedWithCondition(mockPage, condition);

            // then
            assertThat(responses).hasSize(2);
            verify(feedFinder).findAllWithCondition(condition, mockPage.lastId(), mockPage.pageSize());
        }

        @Test
        @DisplayName("조건으로 피드 목록 조회 성공 (페이지 없음)")
        void getAllFeedWithConditionWithoutPage() {
            // given
            LocalDate date = LocalDate.of(2024, 1, 1);
            RequestFindFeedCondition condition = new RequestFindFeedCondition(WRITER_ID, date, FeedStatus.ACTIVE, 10L, null);
            given(feedFinder.findAllWithCondition(condition, null, null))
                    .willReturn(mockFeeds);

            // when
            List<ResponseFeedDto> responses = feedService.getAllFeedWithCondition(null, condition);

            // then
            assertThat(responses).hasSize(2);
            verify(feedFinder).findAllWithCondition(condition, null, null);
        }

        @Test
        @DisplayName("빈 결과 조건으로 피드 조회")
        void getAllFeedWithConditionEmptyResult() {
            // given
            RequestFindFeedCondition condition = new RequestFindFeedCondition(999L, null, FeedStatus.ACTIVE, 10L, null);
            given(feedFinder.findAllWithCondition(condition, mockPage.lastId(), mockPage.pageSize()))
                    .willReturn(Collections.emptyList());

            // when
            List<ResponseFeedDto> responses = feedService.getAllFeedWithCondition(mockPage, condition);

            // then
            assertThat(responses).isEmpty();
        }
    }

    @Nested
    @DisplayName("FeedService 사용자 ID별 피드 목록 조회 테스트")
    class GetFeedsByUserIdsTest {

        @Test
        @DisplayName("사용자 ID 목록으로 피드 조회 성공")
        void getAllFeedWithUserIdsSuccess() {
            // given
            List<Long> userIds = Arrays.asList(1L, 2L, 3L);
            given(feedFinder.findByFollowingIds(userIds, mockPage.lastId(), mockPage.pageSize()))
                    .willReturn(mockFeeds);

            // when
            List<ResponseFeedDto> responses = feedService.getAllFeedWithUserIds(userIds, mockPage);

            // then
            assertThat(responses).hasSize(2);
            verify(feedFinder).findByFollowingIds(userIds, mockPage.lastId(), mockPage.pageSize());
        }

        @Test
        @DisplayName("빈 사용자 ID 목록으로 피드 조회")
        void getAllFeedWithEmptyUserIds() {
            // given
            List<Long> emptyUserIds = Collections.emptyList();
            given(feedFinder.findByFollowingIds(emptyUserIds, mockPage.lastId(), mockPage.pageSize()))
                    .willReturn(Collections.emptyList());

            // when
            List<ResponseFeedDto> responses = feedService.getAllFeedWithUserIds(emptyUserIds, mockPage);

            // then
            assertThat(responses).isEmpty();
        }

        @Test
        @DisplayName("단일 사용자 ID로 피드 조회")
        void getAllFeedWithSingleUserId() {
            // given
            List<Long> singleUserId = Arrays.asList(1L);
            given(feedFinder.findByFollowingIds(singleUserId, mockPage.lastId(), mockPage.pageSize()))
                    .willReturn(Arrays.asList(mockFeed));

            // when
            List<ResponseFeedDto> responses = feedService.getAllFeedWithUserIds(singleUserId, mockPage);

            // then
            assertThat(responses).hasSize(1);
            assertThat(responses.get(0).feedId()).isEqualTo(FEED_ID);
        }
    }

    @Nested
    @DisplayName("FeedService 피드 수정 테스트")
    class UpdateFeedTest {

        @Test
        @DisplayName("피드 수정 성공")
        void updateFeedSuccess() {
            // given
            String newContent = "수정된 내용입니다.";
            FeedUpdateCommand command = new FeedUpdateCommand(FEED_ID, USER_ID, newContent, IMAGE_URLS);

            // when
            feedService.updateFeed(command);

            // then
            verify(feedCommander).updateFeed(command);
        }

        @Test
        @DisplayName("피드 내용만 수정")
        void updateFeedContentOnly() {
            // given
            String newContent = "수정된 내용입니다.";
            FeedUpdateCommand command = new FeedUpdateCommand(FEED_ID, USER_ID, newContent, null);

            // when
            feedService.updateFeed(command);

            // then
            verify(feedCommander).updateFeed(command);
        }

        @Test
        @DisplayName("피드 이미지만 수정")
        void updateFeedImagesOnly() {
            // given
            List<String> newImageUrls = List.of("new_image1.jpg", "new_image2.jpg");
            FeedUpdateCommand command = new FeedUpdateCommand(FEED_ID, USER_ID, null, newImageUrls);

            // when
            feedService.updateFeed(command);

            // then
            verify(feedCommander).updateFeed(command);
        }
    }

    @Nested
    @DisplayName("FeedService 피드 삭제 테스트")
    class DeleteFeedTest {

        @Test
        @DisplayName("사용자에 의한 피드 삭제 성공")
        void deleteFeedSuccess() {
            // given
            FeedDeleteCommand command = new FeedDeleteCommand(FEED_ID, USER_ID);

            // when
            feedService.deleteFeed(command);

            // then
            verify(feedCommander).deleteFeed(command);
        }

        @Test
        @DisplayName("관리자에 의한 피드 삭제 성공")
        void deleteFeedByAdminSuccess() {
            // given
            FeedDeleteByAdminCommand command = new FeedDeleteByAdminCommand(FEED_ID);

            // when
            feedService.deleteFeedByAdmin(command);

            // then
            verify(feedCommander).deleteFeedByAdmin(command);
        }
    }

    @Nested
    @DisplayName("FeedService 경계값 테스트")
    class BoundaryTest {

        @Test
        @DisplayName("null 페이지로 조건 조회")
        void getAllFeedWithConditionNullPage() {
            // given
            RequestFindFeedCondition condition = new RequestFindFeedCondition(WRITER_ID, null, FeedStatus.ACTIVE, 10L, null);
            given(feedFinder.findAllWithCondition(condition, null, null))
                    .willReturn(Collections.emptyList());

            // when
            List<ResponseFeedDto> responses = feedService.getAllFeedWithCondition(null, condition);

            // then
            assertThat(responses).isEmpty();
            verify(feedFinder).findAllWithCondition(condition, null, null);
        }

        @Test
        @DisplayName("0 ID로 피드 조회")
        void getFeedByZeroId() {
            // given
            given(feedFinder.findById(0L)).willThrow(new RuntimeException("Feed not found"));

            // when & then
            assertThatThrownBy(() -> feedService.getFeedById(0L))
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("음수 ID로 피드 조회")
        void getFeedByNegativeId() {
            // given
            given(feedFinder.findById(-1L)).willThrow(new RuntimeException("Feed not found"));

            // when & then
            assertThatThrownBy(() -> feedService.getFeedById(-1L))
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("최대값 ID로 피드 조회")
        void getFeedByMaxId() {
            // given
            Long maxId = Long.MAX_VALUE;
            given(feedFinder.findById(maxId)).willThrow(new RuntimeException("Feed not found"));

            // when & then
            assertThatThrownBy(() -> feedService.getFeedById(maxId))
                    .isInstanceOf(RuntimeException.class);
        }
    }

    @Nested
    @DisplayName("FeedService 트랜잭션 테스트")
    class TransactionTest {

        @Test
        @DisplayName("읽기 전용 트랜잭션에서 조회 작업")
        void readOnlyTransactionForGetOperations() {
            // given
            given(feedFinder.findById(FEED_ID)).willReturn(mockFeed);

            // when
            ResponseFeedDetail response = feedService.getFeedById(FEED_ID);

            // then
            assertThat(response).isNotNull();
            verify(feedFinder).findById(FEED_ID);
        }

        @Test
        @DisplayName("쓰기 트랜잭션에서 생성 작업")
        void writeTransactionForCreateOperation() {
            // given
            FeedCreateCommand command = new FeedCreateCommand(WRITER_ID, CONTENT, IMAGE_URLS);

            // when
            feedService.createFeed(command);

            // then
            verify(feedCommander).createFeed(command);
        }

        @Test
        @DisplayName("쓰기 트랜잭션에서 수정 작업")
        void writeTransactionForUpdateOperation() {
            // given
            FeedUpdateCommand command = new FeedUpdateCommand(FEED_ID, USER_ID, CONTENT, IMAGE_URLS);

            // when
            feedService.updateFeed(command);

            // then
            verify(feedCommander).updateFeed(command);
        }

        @Test
        @DisplayName("쓰기 트랜잭션에서 삭제 작업")
        void writeTransactionForDeleteOperation() {
            // given
            FeedDeleteCommand command = new FeedDeleteCommand(FEED_ID, USER_ID);

            // when
            feedService.deleteFeed(command);

            // then
            verify(feedCommander).deleteFeed(command);
        }
    }
}