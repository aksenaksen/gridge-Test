package com.example.instagram.feed.application;

import com.example.instagram.feed.application.dto.in.FeedCreateCommand;
import com.example.instagram.feed.application.dto.in.FeedDeleteByAdminCommand;
import com.example.instagram.feed.application.dto.in.FeedDeleteCommand;
import com.example.instagram.feed.application.dto.in.FeedUpdateCommand;
import com.example.instagram.feed.application.feed.FeedCommander;
import com.example.instagram.feed.domain.Feed;
import com.example.instagram.feed.domain.FeedStatus;
import com.example.instagram.feed.exception.NotFoundFeedException;
import com.example.instagram.feed.infrastructor.FeedRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedCommanderTest {

    @Mock
    private FeedRepository feedRepository;

    @InjectMocks
    private FeedCommander feedCommander;

    private final Long FEED_ID = 1L;
    private final Long WRITER_ID = 1L;
    private final Long USER_ID = 1L;
    private final String CONTENT = "테스트 피드 내용입니다.";
    private final List<String> IMAGE_URLS = List.of("image1.jpg", "image2.jpg");

    @Nested
    @DisplayName("FeedCommander 피드 생성 테스트")
    class CreateFeedTest {

        @Test
        @DisplayName("피드 생성 성공")
        void createFeedSuccess() {
            // given
            FeedCreateCommand command = new FeedCreateCommand(WRITER_ID, CONTENT, IMAGE_URLS);
            ArgumentCaptor<Feed> feedCaptor = ArgumentCaptor.forClass(Feed.class);

            // when
            feedCommander.createFeed(command);

            // then
            verify(feedRepository).save(feedCaptor.capture());
            Feed savedFeed = feedCaptor.getValue();
            
            assertThat(savedFeed.getWriterId()).isEqualTo(WRITER_ID);
            assertThat(savedFeed.getContent()).isEqualTo(CONTENT);
            assertThat(savedFeed.getStatus()).isEqualTo(FeedStatus.ACTIVE);
            assertThat(savedFeed.getImages()).hasSize(IMAGE_URLS.size());
        }

        @Test
        @DisplayName("이미지 없이 피드 생성 성공")
        void createFeedWithoutImagesSuccess() {
            // given
            FeedCreateCommand command = new FeedCreateCommand(WRITER_ID, CONTENT, List.of());
            ArgumentCaptor<Feed> feedCaptor = ArgumentCaptor.forClass(Feed.class);

            // when
            feedCommander.createFeed(command);

            // then
            verify(feedRepository).save(feedCaptor.capture());
            Feed savedFeed = feedCaptor.getValue();
            
            assertThat(savedFeed.getWriterId()).isEqualTo(WRITER_ID);
            assertThat(savedFeed.getContent()).isEqualTo(CONTENT);
            assertThat(savedFeed.getImages()).isEmpty();
        }

        @Test
        @DisplayName("빈 내용으로 피드 생성 성공")
        void createFeedWithEmptyContentSuccess() {
            // given
            String emptyContent = "";
            FeedCreateCommand command = new FeedCreateCommand(WRITER_ID, emptyContent, IMAGE_URLS);
            ArgumentCaptor<Feed> feedCaptor = ArgumentCaptor.forClass(Feed.class);

            // when
            feedCommander.createFeed(command);

            // then
            verify(feedRepository).save(feedCaptor.capture());
            Feed savedFeed = feedCaptor.getValue();
            
            assertThat(savedFeed.getContent()).isEqualTo(emptyContent);
        }

        @Test
        @DisplayName("null 내용으로 피드 생성")
        void createFeedWithNullContent() {
            // given
            FeedCreateCommand command = new FeedCreateCommand(WRITER_ID, null, IMAGE_URLS);
            ArgumentCaptor<Feed> feedCaptor = ArgumentCaptor.forClass(Feed.class);

            // when
            feedCommander.createFeed(command);

            // then
            verify(feedRepository).save(feedCaptor.capture());
            Feed savedFeed = feedCaptor.getValue();
            
            assertThat(savedFeed.getContent()).isNull();
        }
    }

    @Nested
    @DisplayName("FeedCommander 피드 수정 테스트")
    class UpdateFeedTest {

        private Feed mockFeed;

        @BeforeEach
        void setUp() {
            mockFeed = mock(Feed.class);
        }

        @Test
        @DisplayName("피드 내용 수정 성공")
        void updateFeedContentSuccess() {
            // given
            String newContent = "수정된 피드 내용입니다.";
            FeedUpdateCommand command = new FeedUpdateCommand(FEED_ID, USER_ID, newContent, null);
            given(feedRepository.findById(FEED_ID)).willReturn(Optional.of(mockFeed));

            // when
            feedCommander.updateFeed(command);

            // then
            verify(mockFeed).validateCanBeUpdated(USER_ID);
            verify(mockFeed).updateContent(newContent);
            verify(mockFeed, never()).updateImages(any());
        }

        @Test
        @DisplayName("피드 이미지 수정 성공")
        void updateFeedImagesSuccess() {
            // given
            List<String> newImageUrls = List.of("new_image1.jpg", "new_image2.jpg");
            FeedUpdateCommand command = new FeedUpdateCommand(FEED_ID, USER_ID, null, newImageUrls);
            given(feedRepository.findById(FEED_ID)).willReturn(Optional.of(mockFeed));

            // when
            feedCommander.updateFeed(command);

            // then
            verify(mockFeed).validateCanBeUpdated(USER_ID);
            verify(mockFeed).updateImages(newImageUrls);
            verify(mockFeed, never()).updateContent(any());
        }

        @Test
        @DisplayName("피드 내용과 이미지 모두 수정 성공")
        void updateFeedContentAndImagesSuccess() {
            // given
            String newContent = "수정된 피드 내용입니다.";
            List<String> newImageUrls = List.of("new_image1.jpg", "new_image2.jpg");
            FeedUpdateCommand command = new FeedUpdateCommand(FEED_ID, USER_ID, newContent, newImageUrls);
            given(feedRepository.findById(FEED_ID)).willReturn(Optional.of(mockFeed));

            // when
            feedCommander.updateFeed(command);

            // then
            verify(mockFeed).validateCanBeUpdated(USER_ID);
            verify(mockFeed).updateContent(newContent);
            verify(mockFeed).updateImages(newImageUrls);
        }

        @Test
        @DisplayName("존재하지 않는 피드 수정 시 예외 발생")
        void updateNonExistentFeedThrowsException() {
            // given
            FeedUpdateCommand command = new FeedUpdateCommand(FEED_ID, USER_ID, CONTENT, IMAGE_URLS);
            given(feedRepository.findById(FEED_ID)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> feedCommander.updateFeed(command))
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("내용과 이미지가 모두 null인 경우 수정 없음")
        void updateFeedWithAllNullValues() {
            // given
            FeedUpdateCommand command = new FeedUpdateCommand(FEED_ID, USER_ID, null, null);
            given(feedRepository.findById(FEED_ID)).willReturn(Optional.of(mockFeed));

            // when
            feedCommander.updateFeed(command);

            // then
            verify(mockFeed).validateCanBeUpdated(USER_ID);
            verify(mockFeed, never()).updateContent(any());
            verify(mockFeed, never()).updateImages(any());
        }

        @Test
        @DisplayName("빈 내용으로 피드 수정")
        void updateFeedWithEmptyContent() {
            // given
            String emptyContent = "";
            FeedUpdateCommand command = new FeedUpdateCommand(FEED_ID, USER_ID, emptyContent, null);
            given(feedRepository.findById(FEED_ID)).willReturn(Optional.of(mockFeed));

            // when
            feedCommander.updateFeed(command);

            // then
            verify(mockFeed).updateContent(emptyContent);
        }
    }

    @Nested
    @DisplayName("FeedCommander 피드 삭제 테스트")
    class DeleteFeedTest {

        private Feed mockFeed;

        @BeforeEach
        void setUp() {
            mockFeed = mock(Feed.class);
        }

        @Test
        @DisplayName("사용자에 의한 피드 삭제 성공")
        void deleteFeedByUserSuccess() {
            // given
            FeedDeleteCommand command = new FeedDeleteCommand(FEED_ID, USER_ID);
            given(feedRepository.findById(FEED_ID)).willReturn(Optional.of(mockFeed));

            // when
            feedCommander.deleteFeed(command);

            // then
            verify(mockFeed).delete(USER_ID);
        }

        @Test
        @DisplayName("존재하지 않는 피드 삭제 시 예외 발생")
        void deleteNonExistentFeedThrowsException() {
            // given
            FeedDeleteCommand command = new FeedDeleteCommand(FEED_ID, USER_ID);
            given(feedRepository.findById(FEED_ID)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> feedCommander.deleteFeed(command))
                    .isInstanceOf(NotFoundFeedException.class);
        }
    }

    @Nested
    @DisplayName("FeedCommander 관리자 피드 삭제 테스트")
    class DeleteFeedByAdminTest {

        private Feed mockFeed;

        @BeforeEach
        void setUp() {
            mockFeed = mock(Feed.class);
        }

        @Test
        @DisplayName("관리자에 의한 피드 삭제 성공")
        void deleteFeedByAdminSuccess() {
            // given
            FeedDeleteByAdminCommand command = new FeedDeleteByAdminCommand(FEED_ID);
            given(feedRepository.findById(FEED_ID)).willReturn(Optional.of(mockFeed));

            // when
            feedCommander.deleteFeedByAdmin(command);

            // then
            verify(mockFeed).deleteByAdmin();
        }

        @Test
        @DisplayName("존재하지 않는 피드를 관리자가 삭제 시 예외 발생")
        void deleteNonExistentFeedByAdminThrowsException() {
            // given
            FeedDeleteByAdminCommand command = new FeedDeleteByAdminCommand(FEED_ID);
            given(feedRepository.findById(FEED_ID)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> feedCommander.deleteFeedByAdmin(command))
                    .isInstanceOf(NotFoundFeedException.class);
        }
    }

    @Nested
    @DisplayName("FeedCommander 트랜잭션 테스트")
    class TransactionTest {

        @Test
        @DisplayName("피드 생성 중 예외 발생 시 트랜잭션 롤백 확인")
        void createFeedTransactionRollbackOnException() {
            // given
            FeedCreateCommand command = new FeedCreateCommand(WRITER_ID, CONTENT, IMAGE_URLS);
            doThrow(new RuntimeException("DB Error")).when(feedRepository).save(any());

            // when & then
            assertThatThrownBy(() -> feedCommander.createFeed(command))
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("피드 수정 중 예외 발생 시 트랜잭션 롤백 확인")
        void updateFeedTransactionRollbackOnException() {
            // given
            Feed mockFeed = mock(Feed.class);
            FeedUpdateCommand command = new FeedUpdateCommand(FEED_ID, USER_ID, CONTENT, null);
            given(feedRepository.findById(FEED_ID)).willReturn(Optional.of(mockFeed));
            doThrow(new RuntimeException("Validation Error")).when(mockFeed).validateCanBeUpdated(any());

            // when & then
            assertThatThrownBy(() -> feedCommander.updateFeed(command))
                    .isInstanceOf(RuntimeException.class);
        }
    }

    @Nested
    @DisplayName("FeedCommander 경계값 테스트")
    class BoundaryTest {

        @Test
        @DisplayName("매우 긴 내용으로 피드 생성")
        void createFeedWithVeryLongContent() {
            // given
            String longContent = "a".repeat(10000);
            FeedCreateCommand command = new FeedCreateCommand(WRITER_ID, longContent, IMAGE_URLS);
            ArgumentCaptor<Feed> feedCaptor = ArgumentCaptor.forClass(Feed.class);

            // when
            feedCommander.createFeed(command);

            // then
            verify(feedRepository).save(feedCaptor.capture());
            Feed savedFeed = feedCaptor.getValue();
            assertThat(savedFeed.getContent()).isEqualTo(longContent);
        }

        @Test
        @DisplayName("많은 이미지로 피드 생성")
        void createFeedWithManyImages() {
            // given
            List<String> manyImages = List.of("img1.jpg", "img2.jpg", "img3.jpg", "img4.jpg", "img5.jpg", 
                                             "img6.jpg", "img7.jpg", "img8.jpg", "img9.jpg", "img10.jpg");
            FeedCreateCommand command = new FeedCreateCommand(WRITER_ID, CONTENT, manyImages);
            ArgumentCaptor<Feed> feedCaptor = ArgumentCaptor.forClass(Feed.class);

            // when
            feedCommander.createFeed(command);

            // then
            verify(feedRepository).save(feedCaptor.capture());
            Feed savedFeed = feedCaptor.getValue();
            assertThat(savedFeed.getImages()).hasSize(manyImages.size());
        }

        @Test
        @DisplayName("0 ID로 피드 업데이트")
        void updateFeedWithZeroId() {
            // given
            FeedUpdateCommand command = new FeedUpdateCommand(0L, USER_ID, CONTENT, null);
            given(feedRepository.findById(0L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> feedCommander.updateFeed(command))
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("음수 ID로 피드 삭제")
        void deleteFeedWithNegativeId() {
            // given
            FeedDeleteCommand command = new FeedDeleteCommand(-1L, USER_ID);
            given(feedRepository.findById(-1L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> feedCommander.deleteFeed(command))
                    .isInstanceOf(NotFoundFeedException.class);
        }
    }
}