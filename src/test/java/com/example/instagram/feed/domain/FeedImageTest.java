package com.example.instagram.feed.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class FeedImageTest {

    private Feed feed;
    private final String IMAGE_URL = "https://example.com/image.jpg";
    private final Integer DISPLAY_ORDER = 1;

    @BeforeEach
    void setUp() {
        feed = Feed.builder()
                .writerId(1L)
                .content("Test content")
                .status(FeedStatus.ACTIVE)
                .build();
    }

    @Nested
    @DisplayName("FeedImage 생성 테스트")
    class CreateFeedImageTest {

        @Test
        @DisplayName("빌더를 통한 FeedImage 생성 성공")
        void createFeedImageWithBuilderSuccess() {
            // when
            FeedImage feedImage = FeedImage.builder()
                    .feed(feed)
                    .imageUrl(IMAGE_URL)
                    .displayOrder(DISPLAY_ORDER)
                    .build();

            // then
            assertThat(feedImage.getFeed()).isEqualTo(feed);
            assertThat(feedImage.getImageUrl()).isEqualTo(IMAGE_URL);
            assertThat(feedImage.getDisplayOrder()).isEqualTo(DISPLAY_ORDER);
        }

        @Test
        @DisplayName("필수 필드만으로 FeedImage 생성 성공")
        void createFeedImageWithRequiredFieldsSuccess() {
            // when
            FeedImage feedImage = FeedImage.builder()
                    .imageUrl(IMAGE_URL)
                    .displayOrder(DISPLAY_ORDER)
                    .build();

            // then
            assertThat(feedImage.getImageUrl()).isEqualTo(IMAGE_URL);
            assertThat(feedImage.getDisplayOrder()).isEqualTo(DISPLAY_ORDER);
        }
    }

    @Nested
    @DisplayName("FeedImage 수정 테스트")
    class UpdateFeedImageTest {

        private FeedImage feedImage;

        @BeforeEach
        void setUp() {
            feedImage = FeedImage.builder()
                    .feed(feed)
                    .imageUrl(IMAGE_URL)
                    .displayOrder(DISPLAY_ORDER)
                    .build();
        }

        @Test
        @DisplayName("Feed 설정 성공")
        void setFeedSuccess() {
            // given
            Feed newFeed = Feed.builder()
                    .writerId(2L)
                    .content("New feed content")
                    .status(FeedStatus.ACTIVE)
                    .build();

            // when
            feedImage.setFeed(newFeed);

            // then
            assertThat(feedImage.getFeed()).isEqualTo(newFeed);
        }

        @Test
        @DisplayName("Display Order 수정 성공")
        void updateDisplayOrderSuccess() {
            // given
            Integer newDisplayOrder = 3;

            // when
            feedImage.updateDisplayOrder(newDisplayOrder);

            // then
            assertThat(feedImage.getDisplayOrder()).isEqualTo(newDisplayOrder);
        }

        @Test
        @DisplayName("Display Order를 null로 수정")
        void updateDisplayOrderWithNull() {
            // when
            feedImage.updateDisplayOrder(null);

            // then
            assertThat(feedImage.getDisplayOrder()).isNull();
        }

        @Test
        @DisplayName("Display Order를 0으로 수정")
        void updateDisplayOrderWithZero() {
            // when
            feedImage.updateDisplayOrder(0);

            // then
            assertThat(feedImage.getDisplayOrder()).isEqualTo(0);
        }

        @Test
        @DisplayName("Display Order를 음수로 수정")
        void updateDisplayOrderWithNegative() {
            // when
            feedImage.updateDisplayOrder(-1);

            // then
            assertThat(feedImage.getDisplayOrder()).isEqualTo(-1);
        }
    }

    @Nested
    @DisplayName("FeedImage 연관관계 테스트")
    class FeedImageRelationTest {

        @Test
        @DisplayName("FeedImage가 Feed와 올바르게 연관됨")
        void feedImageFeedRelationSuccess() {
            // given
            FeedImage feedImage = FeedImage.builder()
                    .imageUrl(IMAGE_URL)
                    .displayOrder(DISPLAY_ORDER)
                    .build();

            // when
            feedImage.setFeed(feed);

            // then
            assertThat(feedImage.getFeed()).isEqualTo(feed);
        }

        @Test
        @DisplayName("Feed가 null일 때도 FeedImage 생성 가능")
        void feedImageWithNullFeedSuccess() {
            // when
            FeedImage feedImage = FeedImage.builder()
                    .feed(null)
                    .imageUrl(IMAGE_URL)
                    .displayOrder(DISPLAY_ORDER)
                    .build();

            // then
            assertThat(feedImage.getFeed()).isNull();
            assertThat(feedImage.getImageUrl()).isEqualTo(IMAGE_URL);
            assertThat(feedImage.getDisplayOrder()).isEqualTo(DISPLAY_ORDER);
        }
    }

    @Nested
    @DisplayName("FeedImage 속성 검증 테스트")
    class FeedImageValidationTest {

        @Test
        @DisplayName("긴 이미지 URL 처리")
        void longImageUrlHandling() {
            // given
            String longUrl = "https://example.com/very/long/path/to/image/" + "a".repeat(400) + ".jpg";

            // when
            FeedImage feedImage = FeedImage.builder()
                    .imageUrl(longUrl)
                    .displayOrder(DISPLAY_ORDER)
                    .build();

            // then
            assertThat(feedImage.getImageUrl()).isEqualTo(longUrl);
        }

        @Test
        @DisplayName("빈 문자열 이미지 URL")
        void emptyImageUrl() {
            // when
            FeedImage feedImage = FeedImage.builder()
                    .imageUrl("")
                    .displayOrder(DISPLAY_ORDER)
                    .build();

            // then
            assertThat(feedImage.getImageUrl()).isEqualTo("");
        }

        @Test
        @DisplayName("큰 Display Order 값")
        void largeDisplayOrder() {
            // given
            Integer largeOrder = Integer.MAX_VALUE;

            // when
            FeedImage feedImage = FeedImage.builder()
                    .imageUrl(IMAGE_URL)
                    .displayOrder(largeOrder)
                    .build();

            // then
            assertThat(feedImage.getDisplayOrder()).isEqualTo(largeOrder);
        }
    }
}