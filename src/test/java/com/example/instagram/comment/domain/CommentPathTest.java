package com.example.instagram.comment.domain;

import com.example.instagram.comment.exception.OverCommentLimitException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class CommentPathTest {

    @Nested
    @DisplayName("CommentPath 생성 테스트")
    class CreateCommentPathTest {

        @Test
        @DisplayName("루트 경로로 CommentPath 생성 성공")
        void createRootCommentPathSuccess() {
            // given
            String rootPath = "00000";

            // when
            CommentPath commentPath = CommentPath.create(rootPath);

            // then
            assertThat(commentPath.getPath()).isEqualTo(rootPath);
            assertThat(commentPath.getDepth()).isEqualTo(1);
        }

        @Test
        @DisplayName("자식 경로로 CommentPath 생성 성공")
        void createChildCommentPathSuccess() {
            // given
            String childPath = "0000000001";

            // when
            CommentPath commentPath = CommentPath.create(childPath);

            // then
            assertThat(commentPath.getPath()).isEqualTo(childPath);
            assertThat(commentPath.getDepth()).isEqualTo(2);
        }

        @Test
        @DisplayName("최대 깊이 경로로 CommentPath 생성 성공")
        void createMaxDepthCommentPathSuccess() {
            // given
            String maxDepthPath = "00000000010000100001zzzzz";

            // when
            CommentPath commentPath = CommentPath.create(maxDepthPath);

            // then
            assertThat(commentPath.getPath()).isEqualTo(maxDepthPath);
            assertThat(commentPath.getDepth()).isEqualTo(5);
        }

        @Test
        @DisplayName("깊이 초과 시 예외 발생")
        void createCommentPathOverDepthThrowsException() {
            // given
            String overDepthPath = "000000000100001000010000100001";

            // when & then
            assertThatThrownBy(() -> CommentPath.create(overDepthPath))
                    .isInstanceOf(OverCommentLimitException.class);
        }

        @Test
        @DisplayName("잘못된 길이의 경로로 생성")
        void createCommentPathWithInvalidLength() {
            // given
            String invalidPath = "0000";

            // when
            CommentPath commentPath = CommentPath.create(invalidPath);

            // then
            assertThat(commentPath.getPath()).isEqualTo(invalidPath);
            assertThat(commentPath.getDepth()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("CommentPath 깊이 테스트")
    class DepthTest {

        @Test
        @DisplayName("루트 깊이 계산")
        void calculateRootDepth() {
            // given
            CommentPath commentPath = CommentPath.create("00000");

            // when
            int depth = commentPath.getDepth();

            // then
            assertThat(depth).isEqualTo(1);
        }

        @Test
        @DisplayName("2단계 깊이 계산")
        void calculateSecondDepth() {
            // given
            CommentPath commentPath = CommentPath.create("0000000001");

            // when
            int depth = commentPath.getDepth();

            // then
            assertThat(depth).isEqualTo(2);
        }

        @Test
        @DisplayName("3단계 깊이 계산")
        void calculateThirdDepth() {
            // given
            CommentPath commentPath = CommentPath.create("000000000100001");

            // when
            int depth = commentPath.getDepth();

            // then
            assertThat(depth).isEqualTo(3);
        }

        @Test
        @DisplayName("최대 깊이 계산")
        void calculateMaxDepth() {
            // given
            CommentPath commentPath = CommentPath.create("00000000010000100001zzzzz");

            // when
            int depth = commentPath.getDepth();

            // then
            assertThat(depth).isEqualTo(5);
        }
    }

    @Nested
    @DisplayName("CommentPath isRoot 테스트")
    class IsRootTest {

        @Test
        @DisplayName("루트 경로 확인")
        void isRootPathTrue() {
            // given
            CommentPath commentPath = CommentPath.create("00000");

            // when
            boolean isRoot = commentPath.isRoot();

            // then
            assertThat(isRoot).isTrue();
        }

        @Test
        @DisplayName("자식 경로 확인")
        void isRootPathFalse() {
            // given
            CommentPath commentPath = CommentPath.create("0000000001");

            // when
            boolean isRoot = commentPath.isRoot();

            // then
            assertThat(isRoot).isFalse();
        }

        @Test
        @DisplayName("깊은 자식 경로 확인")
        void isRootDeepPathFalse() {
            // given
            CommentPath commentPath = CommentPath.create("000000000100001");

            // when
            boolean isRoot = commentPath.isRoot();

            // then
            assertThat(isRoot).isFalse();
        }
    }

    @Nested
    @DisplayName("CommentPath 부모 경로 테스트")
    class ParentPathTest {

        @Test
        @DisplayName("자식 경로의 부모 경로 가져오기")
        void getParentPathFromChild() {
            // given
            CommentPath commentPath = CommentPath.create("0000000001");

            // when
            String parentPath = commentPath.getParentPath();

            // then
            assertThat(parentPath).isEqualTo("00000");
        }

        @Test
        @DisplayName("깊은 자식 경로의 부모 경로 가져오기")
        void getParentPathFromDeepChild() {
            // given
            CommentPath commentPath = CommentPath.create("000000000100001");

            // when
            String parentPath = commentPath.getParentPath();

            // then
            assertThat(parentPath).isEqualTo("0000000001");
        }

        @Test
        @DisplayName("루트 경로의 부모 경로 가져오기")
        void getParentPathFromRoot() {
            // given
            CommentPath commentPath = CommentPath.create("00000");

            // when
            String parentPath = commentPath.getParentPath();

            // then
            assertThat(parentPath).isEqualTo("");
        }

        @Test
        @DisplayName("최대 깊이 경로의 부모 경로 가져오기")
        void getParentPathFromMaxDepth() {
            // given
            CommentPath commentPath = CommentPath.create("00000000010000100001zzzzz");

            // when
            String parentPath = commentPath.getParentPath();

            // then
            assertThat(parentPath).isEqualTo("00000000010000100001");
        }
    }

    @Nested
    @DisplayName("CommentPath 자식 경로 생성 테스트")
    class CreateChildCommentPathTest {

        @Test
        @DisplayName("첫 번째 자식 경로 생성")
        void createFirstChildCommentPath() {
            // given
            CommentPath parentPath = CommentPath.create("00000");

            // when
            CommentPath childPath = parentPath.createChildCommentPath(null);

            // then
            assertThat(childPath.getPath()).isEqualTo("0000000000");
            assertThat(childPath.getDepth()).isEqualTo(2);
        }

        @Test
        @DisplayName("기존 자식이 있을 때 다음 자식 경로 생성")
        void createNextChildCommentPath() {
            // given
            CommentPath parentPath = CommentPath.create("00000");
            String existingChildTopPath = "0000000001";

            // when
            CommentPath childPath = parentPath.createChildCommentPath(existingChildTopPath);

            // then
            assertThat(childPath.getPath()).isEqualTo("0000000002");
            assertThat(childPath.getDepth()).isEqualTo(2);
        }

        @Test
        @DisplayName("깊은 레벨에서 자식 경로 생성")
        void createChildCommentPathInDeepLevel() {
            // given
            CommentPath parentPath = CommentPath.create("0000000001");

            // when
            CommentPath childPath = parentPath.createChildCommentPath(null);

            // then
            assertThat(childPath.getPath()).isEqualTo("000000000100000");
            assertThat(childPath.getDepth()).isEqualTo(3);
        }

        @Test
        @DisplayName("최대 깊이에서 자식 경로 생성 시 예외 발생")
        void createChildCommentPathAtMaxDepthThrowsException() {
            // given
            CommentPath parentPath = CommentPath.create("0000000001000010000100001");

            // when & then
            assertThatThrownBy(() -> parentPath.createChildCommentPath(null))
                    .isInstanceOf(OverCommentLimitException.class);
        }

        @Test
        @DisplayName("최대 값에서 자식 경로 생성 시 예외 발생")
        void createChildCommentPathAtMaxValueThrowsException() {
            // given
            CommentPath parentPath = CommentPath.create("00000");
            String maxValuePath = "00000zzzzz";

            // when & then
            assertThatThrownBy(() -> parentPath.createChildCommentPath(maxValuePath))
                    .isInstanceOf(OverCommentLimitException.class);
        }
    }

    @Nested
    @DisplayName("CommentPath 경로 증가 테스트")
    class PathIncrementTest {

        @Test
        @DisplayName("숫자 경로 증가")
        void incrementNumericPath() {
            // given
            CommentPath parentPath = CommentPath.create("00000");
            String existingPath = "0000000009";

            // when
            CommentPath nextPath = parentPath.createChildCommentPath(existingPath);

            // then
            assertThat(nextPath.getPath()).isEqualTo("000000000A");
        }

        @Test
        @DisplayName("문자 경로 증가")
        void incrementAlphabeticPath() {
            // given
            CommentPath parentPath = CommentPath.create("00000");
            String existingPath = "00000A0000";

            // when
            CommentPath nextPath = parentPath.createChildCommentPath(existingPath);

            // then
            assertThat(nextPath.getPath()).isEqualTo("00000A0001");
        }

        @Test
        @DisplayName("혼합 문자 경로 증가")
        void incrementMixedPath() {
            // given
            CommentPath parentPath = CommentPath.create("00000");
            String existingPath = "00000abcde";

            // when
            CommentPath nextPath = parentPath.createChildCommentPath(existingPath);

            // then
            assertThat(nextPath.getPath()).isEqualTo("00000abcdf");
        }
    }

    @Nested
    @DisplayName("CommentPath 경계값 테스트")
    class BoundaryTest {

        @Test
        @DisplayName("최소값 경로 테스트")
        void testMinValuePath() {
            // given
            CommentPath commentPath = CommentPath.create("00000");

            // when
            CommentPath childPath = commentPath.createChildCommentPath(null);

            // then
            assertThat(childPath.getPath()).isEqualTo("0000000000");
        }

        @Test
        @DisplayName("문자셋 경계값 테스트")
        void testCharsetBoundaryValues() {
            // given
            CommentPath parentPath = CommentPath.create("00000");

            // when
            CommentPath childPath1 = parentPath.createChildCommentPath("0000000009");
            CommentPath childPath2 = parentPath.createChildCommentPath("00000A0000");

            // then
            assertThat(childPath1.getPath()).isEqualTo("000000000A");
            assertThat(childPath2.getPath()).isEqualTo("00000A0001");
        }
    }

    @Nested
    @DisplayName("CommentPath 속성 검증 테스트")
    class ValidationTest {

        @Test
        @DisplayName("빈 경로로 CommentPath 생성")
        void createCommentPathWithEmptyPath() {
            // given
            String emptyPath = "";

            // when
            CommentPath commentPath = CommentPath.create(emptyPath);

            // then
            assertThat(commentPath.getPath()).isEqualTo(emptyPath);
            assertThat(commentPath.getDepth()).isEqualTo(0);
        }

        @Test
        @DisplayName("긴 유효한 경로로 CommentPath 생성")
        void createCommentPathWithLongValidPath() {
            // given
            String longPath = "0000000001000020000300004";

            // when
            CommentPath commentPath = CommentPath.create(longPath);

            // then
            assertThat(commentPath.getPath()).isEqualTo(longPath);
            assertThat(commentPath.getDepth()).isEqualTo(5);
        }
    }
}