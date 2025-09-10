package com.example.instagram.feed.domain;

import com.example.instagram.feed.exception.FeedNotActiveException;
import com.example.instagram.feed.exception.NotMatchedWriterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class FeedTest {

    private Feed feed;
    private final Long WRITER_ID = 1L;
    private final Long OTHER_USER_ID = 2L;
    private final String CONTENT = "Test feed content";

    @BeforeEach
    void setUp() {
        feed = Feed.builder()
                .writerId(WRITER_ID)
                .content(CONTENT)
                .status(FeedStatus.ACTIVE)
                .build();
    }

    @Nested
    @DisplayName("피드 생성 테스트")
    class CreateFeedTest {

        @Test
        @DisplayName("피드 정적 팩토리 메소드로 생성 성공")
        void createFeedSuccess() {
            // given
            Long feedId = 1L;

            // when
            Feed createdFeed = Feed.createFeed(feedId, WRITER_ID, CONTENT, FeedStatus.ACTIVE);

            // then
            assertThat(createdFeed.getFeedId()).isEqualTo(feedId);
            assertThat(createdFeed.getWriterId()).isEqualTo(WRITER_ID);
            assertThat(createdFeed.getContent()).isEqualTo(CONTENT);
            assertThat(createdFeed.getStatus()).isEqualTo(FeedStatus.ACTIVE);
        }

        @Test
        @DisplayName("빌더로 피드 생성 성공")
        void builderCreateFeedSuccess() {
            // when
            Feed builtFeed = Feed.builder()
                    .writerId(WRITER_ID)
                    .content(CONTENT)
                    .status(FeedStatus.ACTIVE)
                    .build();

            // then
            assertThat(builtFeed.getWriterId()).isEqualTo(WRITER_ID);
            assertThat(builtFeed.getContent()).isEqualTo(CONTENT);
            assertThat(builtFeed.getStatus()).isEqualTo(FeedStatus.ACTIVE);
        }
    }

    @Nested
    @DisplayName("피드 수정 테스트")
    class UpdateFeedTest {

        @Test
        @DisplayName("피드 내용 수정 성공")
        void updateContentSuccess() {
            // given
            String newContent = "Updated content";

            // when
            feed.updateContent(newContent);

            // then
            assertThat(feed.getContent()).isEqualTo(newContent);
        }

        @Test
        @DisplayName("피드 수정 가능 여부 검증 - 성공")
        void validateCanBeUpdatedSuccess() {
            // when & then
            assertThatNoException().isThrownBy(() -> 
                feed.validateCanBeUpdated(WRITER_ID)
            );
        }

        @Test
        @DisplayName("피드 수정 가능 여부 검증 - 작성자 불일치로 실패")
        void validateCanBeUpdatedFailByNotMatchedWriter() {
            // when & then
            assertThatThrownBy(() -> 
                feed.validateCanBeUpdated(OTHER_USER_ID)
            ).isInstanceOf(NotMatchedWriterException.class);
        }

        @Test
        @DisplayName("피드 수정 가능 여부 검증 - 삭제된 피드로 실패")
        void validateCanBeUpdatedFailByDeletedFeed() {
            // given
            feed.delete(WRITER_ID);

            // when & then
            assertThatThrownBy(() -> 
                feed.validateCanBeUpdated(WRITER_ID)
            ).isInstanceOf(FeedNotActiveException.class);
        }
    }

    @Nested
    @DisplayName("피드 삭제 테스트")
    class DeleteFeedTest {

        @Test
        @DisplayName("사용자에 의한 피드 삭제 성공")
        void deleteByUserSuccess() {
            // when
            feed.delete(WRITER_ID);

            // then
            assertThat(feed.getStatus()).isEqualTo(FeedStatus.USER_DELETED);
        }

        @Test
        @DisplayName("사용자 피드 삭제 시 작성자 불일치로 실패")
        void deleteByUserFailByNotMatchedWriter() {
            // when & then
            assertThatThrownBy(() -> 
                feed.delete(OTHER_USER_ID)
            ).isInstanceOf(NotMatchedWriterException.class);
        }

        @Test
        @DisplayName("관리자에 의한 피드 삭제 성공")
        void deleteByAdminSuccess() {
            // when
            feed.deleteByAdmin();

            // then
            assertThat(feed.getStatus()).isEqualTo(FeedStatus.ADMIN_REMOVED);
        }
    }

    @Nested
    @DisplayName("피드 이미지 테스트")
    class FeedImageTest {

        @Test
        @DisplayName("피드에 이미지 추가 성공")
        void addImageSuccess() {
            // given
            FeedImage feedImage = FeedImage.builder()
                    .imageUrl("https://example.com/image.jpg")
                    .displayOrder(1)
                    .build();

            // when
            feed.addImage(feedImage);

            // then
            assertThat(feed.getImages()).hasSize(1);
            assertThat(feed.getImages().get(0).getImageUrl()).isEqualTo("https://example.com/image.jpg");
            assertThat(feed.getImages().get(0).getFeed()).isEqualTo(feed);
        }

        @Test
        @DisplayName("피드에 다중 이미지 추가 성공")
        void addImagesSuccess() {
            // given
            List<String> imageUrls = Arrays.asList(
                    "https://example.com/image1.jpg",
                    "https://example.com/image2.jpg",
                    "https://example.com/image3.jpg"
            );

            // when
            feed.addImages(imageUrls);

            // then
            assertThat(feed.getImages()).hasSize(3);
            assertThat(feed.getImages().get(0).getDisplayOrder()).isEqualTo(1);
            assertThat(feed.getImages().get(1).getDisplayOrder()).isEqualTo(2);
            assertThat(feed.getImages().get(2).getDisplayOrder()).isEqualTo(3);
        }

        @Test
        @DisplayName("피드 이미지 업데이트 성공")
        void updateImagesSuccess() {
            // given
            feed.addImages(Arrays.asList("https://example.com/old1.jpg", "https://example.com/old2.jpg"));
            List<String> newImageUrls = Arrays.asList("https://example.com/new1.jpg");

            // when
            feed.updateImages(newImageUrls);

            // then
            assertThat(feed.getImages()).hasSize(1);
            assertThat(feed.getImages().get(0).getImageUrl()).isEqualTo("https://example.com/new1.jpg");
        }

        @Test
        @DisplayName("null 이미지 목록으로 이미지 추가 시 아무 작업 안함")
        void addImagesWithNullList() {
            // when
            feed.addImages(null);

            // then
            assertThat(feed.getImages()).isEmpty();
        }

        @Test
        @DisplayName("빈 이미지 목록으로 이미지 추가 시 아무 작업 안함")
        void addImagesWithEmptyList() {
            // when
            feed.addImages(Arrays.asList());

            // then
            assertThat(feed.getImages()).isEmpty();
        }
    }

    @Nested
    @DisplayName("작성자 검증 테스트")
    class ValidWriterTest {

        @Test
        @DisplayName("작성자 검증 성공")
        void validWriterSuccess() {
            // when & then
            assertThatNoException().isThrownBy(() -> 
                feed.validWriter(WRITER_ID)
            );
        }

        @Test
        @DisplayName("작성자 검증 실패")
        void validWriterFail() {
            // when & then
            assertThatThrownBy(() -> 
                feed.validWriter(OTHER_USER_ID)
            ).isInstanceOf(NotMatchedWriterException.class);
        }
    }
}