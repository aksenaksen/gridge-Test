package com.example.instagram.comment.application;

import com.example.instagram.comment.application.dto.in.CommentBlockCommand;
import com.example.instagram.comment.application.dto.in.CommentCreateCommand;
import com.example.instagram.comment.application.dto.in.CommentDeleteCommand;
import com.example.instagram.comment.application.dto.in.CommentUpdateCommand;
import com.example.instagram.comment.application.dto.out.CommentDto;
import com.example.instagram.comment.domain.Comment;
import com.example.instagram.comment.domain.CommentPage;
import com.example.instagram.comment.domain.CommentPath;
import com.example.instagram.comment.domain.CommentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentCommander commentCommander;

    @Mock
    private CommentFinder commentFinder;

    @InjectMocks
    private CommentService commentService;

    private final Long COMMENT_ID = 1L;
    private final Long FEED_ID = 1L;
    private final Long WRITER_ID = 1L;
    private final String CONTENT = "테스트 댓글입니다.";
    private final String LAST_PATH = "00000";
    private final Long PAGE_SIZE = 10L;

    private Comment mockComment;
    private List<Comment> mockComments;
    private CommentPage mockPage;

    @BeforeEach
    void setUp() {
        CommentPath commentPath = CommentPath.create("00000");
        mockComment = Comment.create(COMMENT_ID, CONTENT, FEED_ID, WRITER_ID, commentPath);
        mockComments = Arrays.asList(
                mockComment,
                Comment.create(2L, "두 번째 댓글", FEED_ID, WRITER_ID, commentPath)
        );
        mockPage = new CommentPage(PAGE_SIZE, LAST_PATH);
    }

    @Nested
    @DisplayName("CommentService 댓글 생성 테스트")
    class CreateCommentTest {

        @Test
        @DisplayName("댓글 생성 성공")
        void createCommentSuccess() {
            // given
            CommentCreateCommand command = new CommentCreateCommand(CONTENT, FEED_ID, WRITER_ID, null);

            // when
            commentService.create(command);

            // then
            verify(commentCommander).createComment(command);
        }

        @Test
        @DisplayName("대댓글 생성 성공")
        void createReplyCommentSuccess() {
            // given
            Long parentCommentId = 1L;
            CommentCreateCommand command = new CommentCreateCommand(CONTENT, FEED_ID, WRITER_ID, "00000");

            // when
            commentService.create(command);

            // then
            verify(commentCommander).createComment(command);
        }

        @Test
        @DisplayName("빈 내용으로 댓글 생성")
        void createCommentWithEmptyContent() {
            // given
            CommentCreateCommand command = new CommentCreateCommand("", FEED_ID, WRITER_ID, null);

            // when
            commentService.create(command);

            // then
            verify(commentCommander).createComment(command);
        }

        @Test
        @DisplayName("null 내용으로 댓글 생성")
        void createCommentWithNullContent() {
            // given
            CommentCreateCommand command = new CommentCreateCommand(null, FEED_ID, WRITER_ID, null);

            // when
            commentService.create(command);

            // then
            verify(commentCommander).createComment(command);
        }

        @Test
        @DisplayName("댓글 생성 중 예외 발생")
        void createCommentThrowsException() {
            // given
            CommentCreateCommand command = new CommentCreateCommand(CONTENT, FEED_ID, WRITER_ID, null);
            doThrow(new RuntimeException("Create error")).when(commentCommander).createComment(command);

            // when & then
            assertThatThrownBy(() -> commentService.create(command))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Create error");
        }
    }

    @Nested
    @DisplayName("CommentService 댓글 삭제 테스트")
    class DeleteCommentTest {

        @Test
        @DisplayName("댓글 삭제 성공")
        void deleteCommentSuccess() {
            // given
            CommentDeleteCommand command = new CommentDeleteCommand(COMMENT_ID, WRITER_ID);

            // when
            commentService.delete(command);

            // then
            verify(commentCommander).delete(command);
        }

        @Test
        @DisplayName("댓글 삭제 중 예외 발생")
        void deleteCommentThrowsException() {
            // given
            CommentDeleteCommand command = new CommentDeleteCommand(COMMENT_ID, WRITER_ID);
            doThrow(new RuntimeException("Delete error")).when(commentCommander).delete(command);

            // when & then
            assertThatThrownBy(() -> commentService.delete(command))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Delete error");
        }

        @Test
        @DisplayName("null ID로 댓글 삭제 시도")
        void deleteCommentWithNullId() {
            // given
            CommentDeleteCommand command = new CommentDeleteCommand(null, WRITER_ID);

            // when
            commentService.delete(command);

            // then
            verify(commentCommander).delete(command);
        }

        @Test
        @DisplayName("null 작성자 ID로 댓글 삭제 시도")
        void deleteCommentWithNullWriterId() {
            // given
            CommentDeleteCommand command = new CommentDeleteCommand(COMMENT_ID, null);

            // when
            commentService.delete(command);

            // then
            verify(commentCommander).delete(command);
        }
    }

    @Nested
    @DisplayName("CommentService 댓글 수정 테스트")
    class UpdateCommentTest {

        @Test
        @DisplayName("댓글 수정 성공")
        void updateCommentSuccess() {
            // given
            String newContent = "수정된 댓글입니다.";
            CommentUpdateCommand command = new CommentUpdateCommand(COMMENT_ID, WRITER_ID, newContent);

            // when
            commentService.update(command);

            // then
            verify(commentCommander).updateComment(command);
        }

        @Test
        @DisplayName("댓글 수정 중 예외 발생")
        void updateCommentThrowsException() {
            // given
            CommentUpdateCommand command = new CommentUpdateCommand(COMMENT_ID, WRITER_ID, CONTENT);
            doThrow(new RuntimeException("Update error")).when(commentCommander).updateComment(command);

            // when & then
            assertThatThrownBy(() -> commentService.update(command))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Update error");
        }

        @Test
        @DisplayName("빈 내용으로 댓글 수정")
        void updateCommentWithEmptyContent() {
            // given
            CommentUpdateCommand command = new CommentUpdateCommand(COMMENT_ID, WRITER_ID, "");

            // when
            commentService.update(command);

            // then
            verify(commentCommander).updateComment(command);
        }

        @Test
        @DisplayName("null 내용으로 댓글 수정")
        void updateCommentWithNullContent() {
            // given
            CommentUpdateCommand command = new CommentUpdateCommand(COMMENT_ID, WRITER_ID, null);

            // when
            commentService.update(command);

            // then
            verify(commentCommander).updateComment(command);
        }
    }

    @Nested
    @DisplayName("CommentService 댓글 차단 테스트")
    class BlockCommentTest {

        @Test
        @DisplayName("댓글 차단 성공")
        void blockCommentSuccess() {
            // given
            CommentBlockCommand command = new CommentBlockCommand(COMMENT_ID);

            // when
            commentService.block(command);

            // then
            verify(commentCommander).block(command);
        }

        @Test
        @DisplayName("댓글 차단 중 예외 발생")
        void blockCommentThrowsException() {
            // given
            CommentBlockCommand command = new CommentBlockCommand(COMMENT_ID);
            doThrow(new RuntimeException("Block error")).when(commentCommander).block(command);

            // when & then
            assertThatThrownBy(() -> commentService.block(command))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Block error");
        }

        @Test
        @DisplayName("null ID로 댓글 차단 시도")
        void blockCommentWithNullId() {
            // given
            CommentBlockCommand command = new CommentBlockCommand(null);

            // when
            commentService.block(command);

            // then
            verify(commentCommander).block(command);
        }

        @Test
        @DisplayName("0 ID로 댓글 차단 시도")
        void blockCommentWithZeroId() {
            // given
            CommentBlockCommand command = new CommentBlockCommand(0L);

            // when
            commentService.block(command);

            // then
            verify(commentCommander).block(command);
        }

        @Test
        @DisplayName("음수 ID로 댓글 차단 시도")
        void blockCommentWithNegativeId() {
            // given
            CommentBlockCommand command = new CommentBlockCommand(-1L);

            // when
            commentService.block(command);

            // then
            verify(commentCommander).block(command);
        }
    }

    @Nested
    @DisplayName("CommentService 댓글 목록 조회 테스트")
    class FindAllCommentsTest {

        @Test
        @DisplayName("피드 ID로 댓글 목록 조회 성공")
        void findAllCommentsSuccess() {
            // given
            given(commentFinder.findAll(FEED_ID, LAST_PATH, PAGE_SIZE)).willReturn(mockComments);

            // when
            List<CommentDto> results = commentService.findAll(FEED_ID, mockPage);

            // then
            assertThat(results).hasSize(2);
            assertThat(results.get(0).commentId()).isEqualTo(COMMENT_ID);
            assertThat(results.get(0).content()).isEqualTo(CONTENT);
            verify(commentFinder).findAll(FEED_ID, LAST_PATH, PAGE_SIZE);
        }

        @Test
        @DisplayName("빈 댓글 목록 조회")
        void findAllCommentsEmptyResult() {
            // given
            given(commentFinder.findAll(FEED_ID, LAST_PATH, PAGE_SIZE)).willReturn(Collections.emptyList());

            // when
            List<CommentDto> results = commentService.findAll(FEED_ID, mockPage);

            // then
            assertThat(results).isEmpty();
            verify(commentFinder).findAll(FEED_ID, LAST_PATH, PAGE_SIZE);
        }

        @Test
        @DisplayName("null 피드 ID로 댓글 조회")
        void findAllCommentsWithNullFeedId() {
            // given
            given(commentFinder.findAll(null, LAST_PATH, PAGE_SIZE)).willReturn(Collections.emptyList());

            // when
            List<CommentDto> results = commentService.findAll(null, mockPage);

            // then
            assertThat(results).isEmpty();
            verify(commentFinder).findAll(null, LAST_PATH, PAGE_SIZE);
        }

        @Test
        @DisplayName("댓글 조회 중 예외 발생")
        void findAllCommentsThrowsException() {
            // given
            given(commentFinder.findAll(FEED_ID, LAST_PATH, PAGE_SIZE))
                    .willThrow(new RuntimeException("Find error"));

            // when & then
            assertThatThrownBy(() -> commentService.findAll(FEED_ID, mockPage))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Find error");
        }

        @Test
        @DisplayName("null 페이지 정보로 댓글 조회")
        void findAllCommentsWithNullPage() {
            // given
            CommentPage nullPage = new CommentPage(PAGE_SIZE, null);
            given(commentFinder.findAll(FEED_ID, null, PAGE_SIZE)).willReturn(mockComments);

            // when
            List<CommentDto> results = commentService.findAll(FEED_ID, nullPage);

            // then
            assertThat(results).hasSize(2);
            verify(commentFinder).findAll(FEED_ID, null, PAGE_SIZE);
        }
    }

    @Nested
    @DisplayName("CommentService 댓글 단건 조회 테스트")
    class FindCommentByIdTest {

        @Test
        @DisplayName("댓글 ID로 단건 조회 성공")
        void findCommentByIdSuccess() {
            // given
            given(commentFinder.findById(COMMENT_ID)).willReturn(mockComment);

            // when
            CommentDto result = commentService.findById(COMMENT_ID);

            // then
            assertThat(result).isNotNull();
            assertThat(result.commentId()).isEqualTo(COMMENT_ID);
            assertThat(result.content()).isEqualTo(CONTENT);
            verify(commentFinder).findById(COMMENT_ID);
        }

        @Test
        @DisplayName("존재하지 않는 댓글 ID로 조회 시 예외 발생")
        void findCommentByIdNotFoundThrowsException() {
            // given
            given(commentFinder.findById(COMMENT_ID)).willThrow(new RuntimeException("Comment not found"));

            // when & then
            assertThatThrownBy(() -> commentService.findById(COMMENT_ID))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Comment not found");
        }

        @Test
        @DisplayName("null ID로 댓글 조회")
        void findCommentByNullId() {
            // given
            given(commentFinder.findById(null)).willThrow(new RuntimeException("Invalid comment ID"));

            // when & then
            assertThatThrownBy(() -> commentService.findById(null))
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("0 ID로 댓글 조회")
        void findCommentByZeroId() {
            // given
            given(commentFinder.findById(0L)).willThrow(new RuntimeException("Comment not found"));

            // when & then
            assertThatThrownBy(() -> commentService.findById(0L))
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("음수 ID로 댓글 조회")
        void findCommentByNegativeId() {
            // given
            given(commentFinder.findById(-1L)).willThrow(new RuntimeException("Comment not found"));

            // when & then
            assertThatThrownBy(() -> commentService.findById(-1L))
                    .isInstanceOf(RuntimeException.class);
        }
    }

    @Nested
    @DisplayName("CommentService 댓글 수 조회 테스트")
    class CountCommentsTest {

        @Test
        @DisplayName("피드 ID로 댓글 수 조회 성공")
        void countCommentsByFeedIdSuccess() {
            // given
            Long expectedCount = 5L;
            given(commentFinder.countByFeedId(FEED_ID)).willReturn(expectedCount);

            // when
            Long count = commentService.count(FEED_ID);

            // then
            assertThat(count).isEqualTo(expectedCount);
            verify(commentFinder).countByFeedId(FEED_ID);
        }

        @Test
        @DisplayName("댓글이 없는 피드의 댓글 수 조회")
        void countCommentsForEmptyFeed() {
            // given
            given(commentFinder.countByFeedId(FEED_ID)).willReturn(0L);

            // when
            Long count = commentService.count(FEED_ID);

            // then
            assertThat(count).isEqualTo(0L);
            verify(commentFinder).countByFeedId(FEED_ID);
        }

        @Test
        @DisplayName("null 피드 ID로 댓글 수 조회")
        void countCommentsWithNullFeedId() {
            // given
            given(commentFinder.countByFeedId(null)).willReturn(0L);

            // when
            Long count = commentService.count(null);

            // then
            assertThat(count).isEqualTo(0L);
            verify(commentFinder).countByFeedId(null);
        }

        @Test
        @DisplayName("댓글 수 조회 중 예외 발생")
        void countCommentsThrowsException() {
            // given
            given(commentFinder.countByFeedId(FEED_ID)).willThrow(new RuntimeException("Count error"));

            // when & then
            assertThatThrownBy(() -> commentService.count(FEED_ID))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Count error");
        }

        @Test
        @DisplayName("큰 댓글 수 조회")
        void countLargeNumberOfComments() {
            // given
            Long largeCount = 1000000L;
            given(commentFinder.countByFeedId(FEED_ID)).willReturn(largeCount);

            // when
            Long count = commentService.count(FEED_ID);

            // then
            assertThat(count).isEqualTo(largeCount);
        }
    }

    @Nested
    @DisplayName("CommentService 트랜잭션 테스트")
    class TransactionTest {

        @Test
        @DisplayName("쓰기 트랜잭션에서 생성 작업")
        void writeTransactionForCreateOperation() {
            // given
            CommentCreateCommand command = new CommentCreateCommand(CONTENT, FEED_ID, WRITER_ID, null);

            // when
            commentService.create(command);

            // then
            verify(commentCommander).createComment(command);
        }

        @Test
        @DisplayName("쓰기 트랜잭션에서 수정 작업")
        void writeTransactionForUpdateOperation() {
            // given
            CommentUpdateCommand command = new CommentUpdateCommand(COMMENT_ID, WRITER_ID, CONTENT);

            // when
            commentService.update(command);

            // then
            verify(commentCommander).updateComment(command);
        }

        @Test
        @DisplayName("쓰기 트랜잭션에서 삭제 작업")
        void writeTransactionForDeleteOperation() {
            // given
            CommentDeleteCommand command = new CommentDeleteCommand(COMMENT_ID, WRITER_ID);

            // when
            commentService.delete(command);

            // then
            verify(commentCommander).delete(command);
        }

        @Test
        @DisplayName("쓰기 트랜잭션에서 차단 작업")
        void writeTransactionForBlockOperation() {
            // given
            CommentBlockCommand command = new CommentBlockCommand(COMMENT_ID);

            // when
            commentService.block(command);

            // then
            verify(commentCommander).block(command);
        }

        @Test
        @DisplayName("읽기 전용 트랜잭션에서 조회 작업")
        void readOnlyTransactionForFindOperations() {
            // given
            given(commentFinder.findById(COMMENT_ID)).willReturn(mockComment);
            given(commentFinder.findAll(FEED_ID, LAST_PATH, PAGE_SIZE)).willReturn(mockComments);
            given(commentFinder.countByFeedId(FEED_ID)).willReturn(2L);

            // when
            CommentDto comment = commentService.findById(COMMENT_ID);
            List<CommentDto> comments = commentService.findAll(FEED_ID, mockPage);
            Long count = commentService.count(FEED_ID);

            // then
            assertThat(comment).isNotNull();
            assertThat(comments).hasSize(2);
            assertThat(count).isEqualTo(2L);
        }
    }

    @Nested
    @DisplayName("CommentService 복합 시나리오 테스트")
    class ComplexScenarioTest {

        @Test
        @DisplayName("댓글 생성 후 조회")
        void createAndThenFindComment() {
            // given
            CommentCreateCommand createCommand = new CommentCreateCommand(CONTENT, FEED_ID, WRITER_ID, null);
            given(commentFinder.findById(COMMENT_ID)).willReturn(mockComment);

            // when
            commentService.create(createCommand);
            CommentDto result = commentService.findById(COMMENT_ID);

            // then
            verify(commentCommander).createComment(createCommand);
            assertThat(result.content()).isEqualTo(CONTENT);
        }

        @Test
        @DisplayName("댓글 수정 후 조회")
        void updateAndThenFindComment() {
            // given
            String newContent = "수정된 댓글";
            CommentUpdateCommand updateCommand = new CommentUpdateCommand(COMMENT_ID, WRITER_ID, newContent);
            Comment updatedComment = Comment.create(COMMENT_ID, newContent, FEED_ID, WRITER_ID, CommentPath.create("00000"));
            given(commentFinder.findById(COMMENT_ID)).willReturn(updatedComment);

            // when
            commentService.update(updateCommand);
            CommentDto result = commentService.findById(COMMENT_ID);

            // then
            verify(commentCommander).updateComment(updateCommand);
            assertThat(result.content()).isEqualTo(newContent);
        }

        @Test
        @DisplayName("댓글 생성 후 목록 조회")
        void createAndThenFindAllComments() {
            // given
            CommentCreateCommand createCommand = new CommentCreateCommand(CONTENT, FEED_ID, WRITER_ID, null);
            given(commentFinder.findAll(FEED_ID, LAST_PATH, PAGE_SIZE)).willReturn(mockComments);

            // when
            commentService.create(createCommand);
            List<CommentDto> results = commentService.findAll(FEED_ID, mockPage);

            // then
            verify(commentCommander).createComment(createCommand);
            assertThat(results).hasSize(2);
        }

        @Test
        @DisplayName("댓글 삭제 후 수 조회")
        void deleteAndThenCountComments() {
            // given
            CommentDeleteCommand deleteCommand = new CommentDeleteCommand(COMMENT_ID, WRITER_ID);
            given(commentFinder.countByFeedId(FEED_ID)).willReturn(1L);

            // when
            commentService.delete(deleteCommand);
            Long count = commentService.count(FEED_ID);

            // then
            verify(commentCommander).delete(deleteCommand);
            assertThat(count).isEqualTo(1L);
        }
    }
}