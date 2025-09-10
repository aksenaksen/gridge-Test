package com.example.instagram.subscription.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class FollowRelationTest {

    private final Long FOLLOWER_ID = 1L;
    private final Long FOLLOWEE_ID = 2L;

    @Nested
    @DisplayName("FollowRelation 생성 테스트")
    class CreateFollowRelationTest {

        @Test
        @DisplayName("정적 팩토리 메소드를 통한 FollowRelation 생성 성공")
        void createFollowRelationSuccess() {
            // when
            FollowRelation followRelation = FollowRelation.createRelation(FOLLOWER_ID, FOLLOWEE_ID);

            // then
            assertThat(followRelation.getFollowerId()).isEqualTo(FOLLOWER_ID);
            assertThat(followRelation.getFolloweeId()).isEqualTo(FOLLOWEE_ID);
        }

        @Test
        @DisplayName("동일한 ID로 FollowRelation 생성")
        void createFollowRelationWithSameIds() {
            // when
            FollowRelation followRelation = FollowRelation.createRelation(FOLLOWER_ID, FOLLOWER_ID);

            // then
            assertThat(followRelation.getFollowerId()).isEqualTo(FOLLOWER_ID);
            assertThat(followRelation.getFolloweeId()).isEqualTo(FOLLOWER_ID);
        }

        @Test
        @DisplayName("다른 ID로 FollowRelation 생성")
        void createFollowRelationWithDifferentIds() {
            // given
            Long differentFolloweeId = 3L;

            // when
            FollowRelation followRelation = FollowRelation.createRelation(FOLLOWER_ID, differentFolloweeId);

            // then
            assertThat(followRelation.getFollowerId()).isEqualTo(FOLLOWER_ID);
            assertThat(followRelation.getFolloweeId()).isEqualTo(differentFolloweeId);
        }
    }

    @Nested
    @DisplayName("FollowRelation 속성 검증 테스트")
    class FollowRelationValidationTest {

        @Test
        @DisplayName("null ID로 FollowRelation 생성")
        void createFollowRelationWithNullIds() {
            // when
            FollowRelation followRelation = FollowRelation.createRelation(null, null);

            // then
            assertThat(followRelation.getFollowerId()).isNull();
            assertThat(followRelation.getFolloweeId()).isNull();
        }

        @Test
        @DisplayName("하나만 null ID로 FollowRelation 생성")
        void createFollowRelationWithOneNullId() {
            // when
            FollowRelation followRelation1 = FollowRelation.createRelation(null, FOLLOWEE_ID);
            FollowRelation followRelation2 = FollowRelation.createRelation(FOLLOWER_ID, null);

            // then
            assertThat(followRelation1.getFollowerId()).isNull();
            assertThat(followRelation1.getFolloweeId()).isEqualTo(FOLLOWEE_ID);
            assertThat(followRelation2.getFollowerId()).isEqualTo(FOLLOWER_ID);
            assertThat(followRelation2.getFolloweeId()).isNull();
        }

        @Test
        @DisplayName("0 값으로 FollowRelation 생성")
        void createFollowRelationWithZeroValues() {
            // when
            FollowRelation followRelation = FollowRelation.createRelation(0L, 0L);

            // then
            assertThat(followRelation.getFollowerId()).isEqualTo(0L);
            assertThat(followRelation.getFolloweeId()).isEqualTo(0L);
        }

        @Test
        @DisplayName("음수 값으로 FollowRelation 생성")
        void createFollowRelationWithNegativeValues() {
            // when
            FollowRelation followRelation = FollowRelation.createRelation(-1L, -2L);

            // then
            assertThat(followRelation.getFollowerId()).isEqualTo(-1L);
            assertThat(followRelation.getFolloweeId()).isEqualTo(-2L);
        }

        @Test
        @DisplayName("큰 값으로 FollowRelation 생성")
        void createFollowRelationWithLargeValues() {
            // given
            Long largeFollowerId = Long.MAX_VALUE;
            Long largeFolloweeId = Long.MAX_VALUE - 1;

            // when
            FollowRelation followRelation = FollowRelation.createRelation(largeFollowerId, largeFolloweeId);

            // then
            assertThat(followRelation.getFollowerId()).isEqualTo(largeFollowerId);
            assertThat(followRelation.getFolloweeId()).isEqualTo(largeFolloweeId);
        }
    }

    @Nested
    @DisplayName("FollowRelation 동등성 테스트")
    class FollowRelationEqualityTest {

        @Test
        @DisplayName("동일한 값으로 생성된 FollowRelation 객체 비교")
        void compareFollowRelationsWithSameValues() {
            // given
            FollowRelation followRelation1 = FollowRelation.createRelation(FOLLOWER_ID, FOLLOWEE_ID);
            FollowRelation followRelation2 = FollowRelation.createRelation(FOLLOWER_ID, FOLLOWEE_ID);

            // then
            assertThat(followRelation1.getFollowerId()).isEqualTo(followRelation2.getFollowerId());
            assertThat(followRelation1.getFolloweeId()).isEqualTo(followRelation2.getFolloweeId());
        }

        @Test
        @DisplayName("다른 값으로 생성된 FollowRelation 객체 비교")
        void compareFollowRelationsWithDifferentValues() {
            // given
            FollowRelation followRelation1 = FollowRelation.createRelation(FOLLOWER_ID, FOLLOWEE_ID);
            FollowRelation followRelation2 = FollowRelation.createRelation(2L, 3L);

            // then
            assertThat(followRelation1.getFollowerId()).isNotEqualTo(followRelation2.getFollowerId());
            assertThat(followRelation1.getFolloweeId()).isNotEqualTo(followRelation2.getFolloweeId());
        }

        @Test
        @DisplayName("팔로워 ID만 다른 FollowRelation 객체 비교")
        void compareFollowRelationsWithDifferentFollowerIds() {
            // given
            FollowRelation followRelation1 = FollowRelation.createRelation(FOLLOWER_ID, FOLLOWEE_ID);
            FollowRelation followRelation2 = FollowRelation.createRelation(3L, FOLLOWEE_ID);

            // then
            assertThat(followRelation1.getFollowerId()).isNotEqualTo(followRelation2.getFollowerId());
            assertThat(followRelation1.getFolloweeId()).isEqualTo(followRelation2.getFolloweeId());
        }

        @Test
        @DisplayName("팔로위 ID만 다른 FollowRelation 객체 비교")
        void compareFollowRelationsWithDifferentFolloweeIds() {
            // given
            FollowRelation followRelation1 = FollowRelation.createRelation(FOLLOWER_ID, FOLLOWEE_ID);
            FollowRelation followRelation2 = FollowRelation.createRelation(FOLLOWER_ID, 3L);

            // then
            assertThat(followRelation1.getFollowerId()).isEqualTo(followRelation2.getFollowerId());
            assertThat(followRelation1.getFolloweeId()).isNotEqualTo(followRelation2.getFolloweeId());
        }
    }

    @Nested
    @DisplayName("FollowRelation 관계 시나리오 테스트")
    class RelationshipScenarioTest {

        @Test
        @DisplayName("상호 팔로우 관계 생성")
        void createMutualFollowRelationship() {
            // when
            FollowRelation relation1 = FollowRelation.createRelation(FOLLOWER_ID, FOLLOWEE_ID);
            FollowRelation relation2 = FollowRelation.createRelation(FOLLOWEE_ID, FOLLOWER_ID);

            // then
            assertThat(relation1.getFollowerId()).isEqualTo(relation2.getFolloweeId());
            assertThat(relation1.getFolloweeId()).isEqualTo(relation2.getFollowerId());
        }

        @Test
        @DisplayName("일방향 팔로우 관계 생성")
        void createOneWayFollowRelationship() {
            // given
            Long thirdUserId = 3L;

            // when
            FollowRelation relation = FollowRelation.createRelation(FOLLOWER_ID, thirdUserId);

            // then
            assertThat(relation.getFollowerId()).isEqualTo(FOLLOWER_ID);
            assertThat(relation.getFolloweeId()).isEqualTo(thirdUserId);
        }

        @Test
        @DisplayName("자기 자신을 팔로우하는 관계 생성")
        void createSelfFollowRelationship() {
            // when
            FollowRelation selfRelation = FollowRelation.createRelation(FOLLOWER_ID, FOLLOWER_ID);

            // then
            assertThat(selfRelation.getFollowerId()).isEqualTo(selfRelation.getFolloweeId());
            assertThat(selfRelation.getFollowerId()).isEqualTo(FOLLOWER_ID);
        }
    }

    @Nested
    @DisplayName("FollowRelation getter 메소드 테스트")
    class GetterMethodTest {

        @Test
        @DisplayName("getFollowerId 메소드 테스트")
        void getFollowerIdTest() {
            // given
            FollowRelation followRelation = FollowRelation.createRelation(FOLLOWER_ID, FOLLOWEE_ID);

            // when
            Long followerId = followRelation.getFollowerId();

            // then
            assertThat(followerId).isEqualTo(FOLLOWER_ID);
        }

        @Test
        @DisplayName("getFolloweeId 메소드 테스트")
        void getFolloweeIdTest() {
            // given
            FollowRelation followRelation = FollowRelation.createRelation(FOLLOWER_ID, FOLLOWEE_ID);

            // when
            Long followeeId = followRelation.getFolloweeId();

            // then
            assertThat(followeeId).isEqualTo(FOLLOWEE_ID);
        }

        @Test
        @DisplayName("null 값에 대한 getter 메소드 테스트")
        void getterMethodsWithNullValues() {
            // given
            FollowRelation followRelation = FollowRelation.createRelation(null, null);

            // when
            Long followerId = followRelation.getFollowerId();
            Long followeeId = followRelation.getFolloweeId();

            // then
            assertThat(followerId).isNull();
            assertThat(followeeId).isNull();
        }
    }
}