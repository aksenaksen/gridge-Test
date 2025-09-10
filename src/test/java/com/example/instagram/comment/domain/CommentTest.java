package com.example.instagram.comment.domain;

import com.example.instagram.comment.application.dto.in.CommentUpdateCommand;
import com.example.instagram.comment.exception.AlreadyBlockedOrDeletedException;
import com.example.instagram.comment.exception.CommentNotMatchedUserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class CommentTest {

    private final Long COMMENT_ID = 1L;
    private final String CONTENT = "테스트 댓글입니다.";
    private final Long FEED_ID = 1L;
    private final Long WRITER_ID = 1L;
    private CommentPath commentPath;

    @BeforeEach
    void setUp() {
        commentPath = CommentPath.create("00000");
    }

    @Nested
    @DisplayName("Comment 생성 테스트")
    class CreateCommentTest {

        @Test
        @DisplayName("정적 팩토리 메소드를 통한 Comment 생성 성공")
        void createCommentSuccess() {
            // when
            Comment comment = Comment.create(COMMENT_ID, CONTENT, FEED_ID, WRITER_ID, commentPath);

            // then
            assertThat(comment.getCommentId()).isEqualTo(COMMENT_ID);
            assertThat(comment.getContent()).isEqualTo(CONTENT);
            assertThat(comment.getFeedId()).isEqualTo(FEED_ID);
            assertThat(comment.getWriterId()).isEqualTo(WRITER_ID);
            assertThat(comment.getCommentPath()).isEqualTo(commentPath);
            assertThat(comment.getStatus()).isEqualTo(CommentStatus.ACTIVE);
        }

        @Test
        @DisplayName("긴 내용으로 Comment 생성 성공")
        void createCommentWithLongContentSuccess() {
            // given
            String longContent = "a".repeat(1000);

            // when
            Comment comment = Comment.create(COMMENT_ID, longContent, FEED_ID, WRITER_ID, commentPath);

            // then
            assertThat(comment.getContent()).isEqualTo(longContent);
            assertThat(comment.getStatus()).isEqualTo(CommentStatus.ACTIVE);
        }

        @Test
        @DisplayName("빈 내용으로 Comment 생성 성공")
        void createCommentWithEmptyContentSuccess() {
            // given
            String emptyContent = "";

            // when
            Comment comment = Comment.create(COMMENT_ID, emptyContent, FEED_ID, WRITER_ID, commentPath);

            // then
            assertThat(comment.getContent()).isEqualTo(emptyContent);
            assertThat(comment.getStatus()).isEqualTo(CommentStatus.ACTIVE);
        }
    }

    @Nested
    @DisplayName("Comment isRoot 테스트")
    class IsRootTest {

        @Test
        @DisplayName("루트 댓글인지 확인")
        void isRootCommentTrue() {
            // given
            CommentPath rootPath = CommentPath.create("00000");
            Comment comment = Comment.create(COMMENT_ID, CONTENT, FEED_ID, WRITER_ID, rootPath);

            // when
            boolean isRoot = comment.isRoot();

            // then
            assertThat(isRoot).isTrue();
        }

        @Test
        @DisplayName("대댓글인지 확인")
        void isRootCommentFalse() {
            // given
            CommentPath childPath = CommentPath.create("0000000001");
            Comment comment = Comment.create(COMMENT_ID, CONTENT, FEED_ID, WRITER_ID, childPath);

            // when
            boolean isRoot = comment.isRoot();

            // then
            assertThat(isRoot).isFalse();
        }
    }

    @Nested
    @DisplayName("Comment 업데이트 테스트")
    class UpdateCommentTest {

        private Comment comment;

        @BeforeEach
        void setUp() {
            comment = Comment.create(COMMENT_ID, CONTENT, FEED_ID, WRITER_ID, commentPath);
        }

        @Test
        @DisplayName("댓글 내용 업데이트 성공")
        void updateCommentContentSuccess() {
            // given
            String newContent = "수정된 댓글입니다.";

            // when
            comment.updateComment(newContent);

            // then
            assertThat(comment.getContent()).isEqualTo(newContent);
        }

        @Test
        @DisplayName("삭제된 댓글 업데이트 시 예외 발생")
        void updateDeletedCommentThrowsException() {
            // given
            comment.delete();

            // when & then
            assertThatThrownBy(() -> comment.updateComment("새로운 내용"))
                    .isInstanceOf(AlreadyBlockedOrDeletedException.class);
        }

        @Test
        @DisplayName("차단된 댓글 업데이트 시 예외 발생")
        void updateBlockedCommentThrowsException() {
            // given
            comment.block();

            // when & then
            assertThatThrownBy(() -> comment.updateComment("새로운 내용"))
                    .isInstanceOf(AlreadyBlockedOrDeletedException.class);
        }

        @Test
        @DisplayName("빈 내용으로 업데이트 성공")
        void updateCommentWithEmptyContentSuccess() {
            // given
            String emptyContent = "";

            // when
            comment.updateComment(emptyContent);

            // then
            assertThat(comment.getContent()).isEqualTo(emptyContent);
        }
    }

    @Nested
    @DisplayName("Comment 삭제 테스트")
    class DeleteCommentTest {

        private Comment comment;

        @BeforeEach
        void setUp() {
            comment = Comment.create(COMMENT_ID, CONTENT, FEED_ID, WRITER_ID, commentPath);
        }

        @Test
        @DisplayName("댓글 삭제 성공")
        void deleteCommentSuccess() {
            // when
            comment.delete();

            // then
            assertThat(comment.getStatus()).isEqualTo(CommentStatus.DELETED_BY_USER);
            assertThat(comment.isDeleted()).isTrue();
        }

        @Test
        @DisplayName("이미 삭제된 댓글 재삭제")
        void deleteAlreadyDeletedComment() {
            // given
            comment.delete();

            // when
            comment.delete();

            // then
            assertThat(comment.getStatus()).isEqualTo(CommentStatus.DELETED_BY_USER);
            assertThat(comment.isDeleted()).isTrue();
        }
    }

    @Nested
    @DisplayName("Comment 차단 테스트")
    class BlockCommentTest {

        private Comment comment;

        @BeforeEach
        void setUp() {
            comment = Comment.create(COMMENT_ID, CONTENT, FEED_ID, WRITER_ID, commentPath);
        }

        @Test
        @DisplayName("댓글 차단 성공")
        void blockCommentSuccess() {
            // when
            comment.block();

            // then
            assertThat(comment.getStatus()).isEqualTo(CommentStatus.BLOCKED);
            assertThat(comment.isBlocked()).isTrue();
        }

        @Test
        @DisplayName("이미 차단된 댓글 재차단")
        void blockAlreadyBlockedComment() {
            // given
            comment.block();

            // when
            comment.block();

            // then
            assertThat(comment.getStatus()).isEqualTo(CommentStatus.BLOCKED);
            assertThat(comment.isBlocked()).isTrue();
        }
    }

    @Nested
    @DisplayName("Comment 상태 확인 테스트")
    class CommentStatusTest {

        private Comment comment;

        @BeforeEach
        void setUp() {
            comment = Comment.create(COMMENT_ID, CONTENT, FEED_ID, WRITER_ID, commentPath);
        }

        @Test
        @DisplayName("활성 상태 댓글 확인")
        void activeCommentStatusCheck() {
            // then
            assertThat(comment.isDeleted()).isFalse();
            assertThat(comment.isBlocked()).isFalse();
        }

        @Test
        @DisplayName("삭제된 댓글 상태 확인")
        void deletedCommentStatusCheck() {
            // when
            comment.delete();

            // then
            assertThat(comment.isDeleted()).isTrue();
            assertThat(comment.isBlocked()).isFalse();
        }

        @Test
        @DisplayName("차단된 댓글 상태 확인")
        void blockedCommentStatusCheck() {
            // when
            comment.block();

            // then
            assertThat(comment.isDeleted()).isFalse();
            assertThat(comment.isBlocked()).isTrue();
        }
    }

    @Nested
    @DisplayName("Comment 소유자 확인 테스트")
    class OwnerValidationTest {

        private Comment comment;

        @BeforeEach
        void setUp() {
            comment = Comment.create(COMMENT_ID, CONTENT, FEED_ID, WRITER_ID, commentPath);
        }

        @Test
        @DisplayName("올바른 소유자 확인 성공")
        void validOwnerCheckSuccess() {
            // when & then
            assertThatCode(() -> comment.isOwner(WRITER_ID))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("잘못된 소유자 확인 시 예외 발생")
        void invalidOwnerCheckThrowsException() {
            // given
            Long differentWriterId = 2L;

            // when & then
            assertThatThrownBy(() -> comment.isOwner(differentWriterId))
                    .isInstanceOf(CommentNotMatchedUserException.class);
        }

        @Test
        @DisplayName("null 소유자 확인 시 예외 발생")
        void nullOwnerCheckThrowsException() {
            // when & then
            assertThatThrownBy(() -> comment.isOwner(null))
                    .isInstanceOf(CommentNotMatchedUserException.class);
        }
    }

    @Nested
    @DisplayName("Comment 속성 검증 테스트")
    class CommentValidationTest {

        @Test
        @DisplayName("null 값들로 Comment 생성")
        void createCommentWithNullValues() {
            // when
            Comment comment = Comment.create(null, null, null, null, commentPath);

            // then
            assertThat(comment.getCommentId()).isNull();
            assertThat(comment.getContent()).isNull();
            assertThat(comment.getFeedId()).isNull();
            assertThat(comment.getWriterId()).isNull();
            assertThat(comment.getCommentPath()).isEqualTo(commentPath);
            assertThat(comment.getStatus()).isEqualTo(CommentStatus.ACTIVE);
        }

        @Test
        @DisplayName("0 값들로 Comment 생성")
        void createCommentWithZeroValues() {
            // when
            Comment comment = Comment.create(0L, CONTENT, 0L, 0L, commentPath);

            // then
            assertThat(comment.getCommentId()).isEqualTo(0L);
            assertThat(comment.getFeedId()).isEqualTo(0L);
            assertThat(comment.getWriterId()).isEqualTo(0L);
        }

        @Test
        @DisplayName("음수 ID로 Comment 생성")
        void createCommentWithNegativeIds() {
            // when
            Comment comment = Comment.create(-1L, CONTENT, -1L, -1L, commentPath);

            // then
            assertThat(comment.getCommentId()).isEqualTo(-1L);
            assertThat(comment.getFeedId()).isEqualTo(-1L);
            assertThat(comment.getWriterId()).isEqualTo(-1L);
        }

        @Test
        @DisplayName("큰 값으로 Comment 생성")
        void createCommentWithLargeValues() {
            // given
            Long largeId = Long.MAX_VALUE;

            // when
            Comment comment = Comment.create(largeId, CONTENT, largeId, largeId, commentPath);

            // then
            assertThat(comment.getCommentId()).isEqualTo(largeId);
            assertThat(comment.getFeedId()).isEqualTo(largeId);
            assertThat(comment.getWriterId()).isEqualTo(largeId);
        }
    }
}