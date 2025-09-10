package com.example.instagram.subscription.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class SubscriptionTest {

    private final Long FOLLOWER_ID = 1L;
    private final Long FOLLOWEE_ID = 2L;

    @Nested
    @DisplayName("Subscription 생성 테스트")
    class CreateSubscriptionTest {

        @Test
        @DisplayName("정적 팩토리 메소드를 통한 Subscription 생성 성공")
        void createSubscriptionSuccess() {
            // when
            Subscription subscription = Subscription.createSubscription(FOLLOWER_ID, FOLLOWEE_ID);

            // then
            assertThat(subscription.getFollowRelation().getFollowerId()).isEqualTo(FOLLOWER_ID);
            assertThat(subscription.getFollowRelation().getFolloweeId()).isEqualTo(FOLLOWEE_ID);
            assertThat(subscription.getFollowStatus()).isEqualTo(FollowStatus.FOLLOWING);
            assertThat(subscription.getStartAt()).isNotNull();
            assertThat(subscription.getEndAt()).isNull();
        }

        @Test
        @DisplayName("생성 시 현재 시간이 startAt에 설정됨")
        void createSubscriptionWithCurrentTime() {
            // given
            LocalDateTime before = LocalDateTime.now().minusSeconds(1);
            
            // when
            Subscription subscription = Subscription.createSubscription(FOLLOWER_ID, FOLLOWEE_ID);
            
            // then
            LocalDateTime after = LocalDateTime.now().plusSeconds(1);
            assertThat(subscription.getStartAt()).isBetween(before, after);
        }

        @Test
        @DisplayName("동일한 ID로 Subscription 생성")
        void createSubscriptionWithSameIds() {
            // when
            Subscription subscription = Subscription.createSubscription(FOLLOWER_ID, FOLLOWER_ID);

            // then
            assertThat(subscription.getFollowRelation().getFollowerId()).isEqualTo(FOLLOWER_ID);
            assertThat(subscription.getFollowRelation().getFolloweeId()).isEqualTo(FOLLOWER_ID);
        }

        @Test
        @DisplayName("null ID로 Subscription 생성")
        void createSubscriptionWithNullIds() {
            // when
            Subscription subscription = Subscription.createSubscription(null, null);

            // then
            assertThat(subscription.getFollowRelation().getFollowerId()).isNull();
            assertThat(subscription.getFollowRelation().getFolloweeId()).isNull();
            assertThat(subscription.getFollowStatus()).isEqualTo(FollowStatus.FOLLOWING);
        }
    }

    @Nested
    @DisplayName("Subscription 상태 확인 테스트")
    class SubscriptionStatusTest {

        private Subscription subscription;

        @BeforeEach
        void setUp() {
            subscription = Subscription.createSubscription(FOLLOWER_ID, FOLLOWEE_ID);
        }

        @Test
        @DisplayName("팔로잉 상태 확인")
        void isFollowingStatusCheck() {
            // then
            assertThat(subscription.isFollowing()).isTrue();
            assertThat(subscription.isBlocked()).isFalse();
            assertThat(subscription.isUnFollowed()).isFalse();
        }

        @Test
        @DisplayName("언팔로우 상태 확인")
        void isUnFollowedStatusCheck() {
            // when
            subscription.unfollow();

            // then
            assertThat(subscription.isFollowing()).isFalse();
            assertThat(subscription.isBlocked()).isFalse();
            assertThat(subscription.isUnFollowed()).isTrue();
        }

        @Test
        @DisplayName("차단 상태 확인")
        void isBlockedStatusCheck() {
            // when
            subscription.blockFollowingUser();

            // then
            assertThat(subscription.isFollowing()).isFalse();
            assertThat(subscription.isBlocked()).isTrue();
            assertThat(subscription.isUnFollowed()).isFalse();
        }
    }

    @Nested
    @DisplayName("Subscription 언팔로우 테스트")
    class UnfollowTest {

        private Subscription subscription;

        @BeforeEach
        void setUp() {
            subscription = Subscription.createSubscription(FOLLOWER_ID, FOLLOWEE_ID);
        }

        @Test
        @DisplayName("팔로잉 상태에서 언팔로우 성공")
        void unfollowFromFollowingSuccess() {
            // when
            subscription.unfollow();

            // then
            assertThat(subscription.getFollowStatus()).isEqualTo(FollowStatus.UNFOLLOWED);
            assertThat(subscription.getEndAt()).isNotNull();
            assertThat(subscription.isUnFollowed()).isTrue();
        }

        @Test
        @DisplayName("언팔로우 시 endAt이 현재 시간으로 설정됨")
        void unfollowSetsEndAtToCurrentTime() {
            // given
            LocalDateTime before = LocalDateTime.now().minusSeconds(1);
            
            // when
            subscription.unfollow();
            
            // then
            LocalDateTime after = LocalDateTime.now().plusSeconds(1);
            assertThat(subscription.getEndAt()).isBetween(before, after);
        }

        @Test
        @DisplayName("이미 언팔로우된 상태에서 재언팔로우")
        void unfollowAlreadyUnfollowed() {
            // given
            subscription.unfollow();
            LocalDateTime firstEndAt = subscription.getEndAt();

            // when
            subscription.unfollow();

            // then
            assertThat(subscription.getFollowStatus()).isEqualTo(FollowStatus.UNFOLLOWED);
            assertThat(subscription.getEndAt()).isAfter(firstEndAt);
        }
    }

    @Nested
    @DisplayName("Subscription 차단 테스트")
    class BlockTest {

        private Subscription subscription;

        @BeforeEach
        void setUp() {
            subscription = Subscription.createSubscription(FOLLOWER_ID, FOLLOWEE_ID);
        }

        @Test
        @DisplayName("팔로잉 상태에서 차단 성공")
        void blockFromFollowingSuccess() {
            // when
            subscription.blockFollowingUser();

            // then
            assertThat(subscription.getFollowStatus()).isEqualTo(FollowStatus.BLOCKED_BY_FOLLOWEE);
            assertThat(subscription.getEndAt()).isNotNull();
            assertThat(subscription.isBlocked()).isTrue();
        }

        @Test
        @DisplayName("차단 시 endAt이 현재 시간으로 설정됨")
        void blockSetsEndAtToCurrentTime() {
            // given
            LocalDateTime before = LocalDateTime.now().minusSeconds(1);
            
            // when
            subscription.blockFollowingUser();
            
            // then
            LocalDateTime after = LocalDateTime.now().plusSeconds(1);
            assertThat(subscription.getEndAt()).isBetween(before, after);
        }

        @Test
        @DisplayName("언팔로우 상태에서 차단")
        void blockFromUnfollowed() {
            // given
            subscription.unfollow();

            // when
            subscription.blockFollowingUser();

            // then
            assertThat(subscription.getFollowStatus()).isEqualTo(FollowStatus.BLOCKED_BY_FOLLOWEE);
            assertThat(subscription.isBlocked()).isTrue();
        }
    }

    @Nested
    @DisplayName("Subscription 팔로우 테스트")
    class FollowTest {

        @Test
        @DisplayName("언팔로우 상태에서 다시 팔로우 성공")
        void followFromUnfollowedSuccess() {
            // given
            Subscription subscription = Subscription.createSubscription(FOLLOWER_ID, FOLLOWEE_ID);
            subscription.unfollow();

            // when
            subscription.follow();

            // then
            assertThat(subscription.getFollowStatus()).isEqualTo(FollowStatus.FOLLOWING);
            assertThat(subscription.isFollowing()).isTrue();
        }

        @Test
        @DisplayName("팔로우 시 startAt이 현재 시간으로 재설정됨")
        void followResetsStartAtToCurrentTime() {
            // given
            Subscription subscription = Subscription.createSubscription(FOLLOWER_ID, FOLLOWEE_ID);
            LocalDateTime originalStartAt = subscription.getStartAt();
            subscription.unfollow();
            
            LocalDateTime before = LocalDateTime.now().minusSeconds(1);
            
            // when
            subscription.follow();
            
            // then
            LocalDateTime after = LocalDateTime.now().plusSeconds(1);
            assertThat(subscription.getStartAt()).isBetween(before, after);
            assertThat(subscription.getStartAt()).isAfter(originalStartAt);
        }

        @Test
        @DisplayName("이미 팔로잉 상태에서 재팔로우")
        void followAlreadyFollowing() {
            // given
            Subscription subscription = Subscription.createSubscription(FOLLOWER_ID, FOLLOWEE_ID);
            LocalDateTime originalStartAt = subscription.getStartAt();

            // when
            subscription.follow();

            // then
            assertThat(subscription.getFollowStatus()).isEqualTo(FollowStatus.FOLLOWING);
            assertThat(subscription.getStartAt()).isAfter(originalStartAt);
        }
    }

    @Nested
    @DisplayName("Subscription 유효성 검증 테스트")
    class ValidationTest {

        @Test
        @DisplayName("팔로잉 상태에서 팔로우 가능성 검증 - 예외 발생")
        void canFollowFromFollowingThrowsException() {
            // given
            Subscription subscription = Subscription.createSubscription(FOLLOWER_ID, FOLLOWEE_ID);

            // when & then
            assertThatThrownBy(() -> subscription.canFollow())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Cannot follow this subscription");
        }

        @Test
        @DisplayName("차단 상태에서 팔로우 가능성 검증 - 예외 발생")
        void canFollowFromBlockedThrowsException() {
            // given
            Subscription subscription = Subscription.createSubscription(FOLLOWER_ID, FOLLOWEE_ID);
            subscription.blockFollowingUser();

            // when & then
            assertThatThrownBy(() -> subscription.canFollow())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Cannot follow this subscription");
        }

        @Test
        @DisplayName("언팔로우 상태에서 언팔로우 가능성 검증 - 예외 발생")
        void canUnFollowFromUnfollowedThrowsException() {
            // given
            Subscription subscription = Subscription.createSubscription(FOLLOWER_ID, FOLLOWEE_ID);
            subscription.unfollow();

            // when & then
            assertThatThrownBy(() -> subscription.canUnFollow())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Cannot follow this subscription");
        }

        @Test
        @DisplayName("차단 상태에서 언팔로우 가능성 검증 - 예외 발생")
        void canUnFollowFromBlockedThrowsException() {
            // given
            Subscription subscription = Subscription.createSubscription(FOLLOWER_ID, FOLLOWEE_ID);
            subscription.blockFollowingUser();

            // when & then
            assertThatThrownBy(() -> subscription.canUnFollow())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Cannot follow this subscription");
        }

        @Test
        @DisplayName("팔로잉 상태에서 언팔로우 가능성 검증 - 성공")
        void canUnFollowFromFollowingSuccess() {
            // given
            Subscription subscription = Subscription.createSubscription(FOLLOWER_ID, FOLLOWEE_ID);

            // when & then
            assertThatCode(() -> subscription.canUnFollow())
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("차단 상태에서 차단 가능성 검증 - 예외 발생")
        void canBlockFromBlockedThrowsException() {
            // given
            Subscription subscription = Subscription.createSubscription(FOLLOWER_ID, FOLLOWEE_ID);
            subscription.blockFollowingUser();

            // when & then
            assertThatThrownBy(() -> subscription.canBlock())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Cannot block this subscription");
        }

        @Test
        @DisplayName("팔로잉 상태에서 차단 가능성 검증 - 성공")
        void canBlockFromFollowingSuccess() {
            // given
            Subscription subscription = Subscription.createSubscription(FOLLOWER_ID, FOLLOWEE_ID);

            // when & then
            assertThatCode(() -> subscription.canBlock())
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("언팔로우 상태에서 차단 가능성 검증 - 성공")
        void canBlockFromUnfollowedSuccess() {
            // given
            Subscription subscription = Subscription.createSubscription(FOLLOWER_ID, FOLLOWEE_ID);
            subscription.unfollow();

            // when & then
            assertThatCode(() -> subscription.canBlock())
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("Subscription 속성 검증 테스트")
    class SubscriptionAttributeTest {

        @Test
        @DisplayName("0 값으로 Subscription 생성")
        void createSubscriptionWithZeroValues() {
            // when
            Subscription subscription = Subscription.createSubscription(0L, 0L);

            // then
            assertThat(subscription.getFollowRelation().getFollowerId()).isEqualTo(0L);
            assertThat(subscription.getFollowRelation().getFolloweeId()).isEqualTo(0L);
        }

        @Test
        @DisplayName("음수 값으로 Subscription 생성")
        void createSubscriptionWithNegativeValues() {
            // when
            Subscription subscription = Subscription.createSubscription(-1L, -1L);

            // then
            assertThat(subscription.getFollowRelation().getFollowerId()).isEqualTo(-1L);
            assertThat(subscription.getFollowRelation().getFolloweeId()).isEqualTo(-1L);
        }

        @Test
        @DisplayName("큰 값으로 Subscription 생성")
        void createSubscriptionWithLargeValues() {
            // given
            Long largeId = Long.MAX_VALUE;

            // when
            Subscription subscription = Subscription.createSubscription(largeId, largeId);

            // then
            assertThat(subscription.getFollowRelation().getFollowerId()).isEqualTo(largeId);
            assertThat(subscription.getFollowRelation().getFolloweeId()).isEqualTo(largeId);
        }
    }

    @Nested
    @DisplayName("Subscription 상태 변경 시나리오 테스트")
    class StatusTransitionTest {

        @Test
        @DisplayName("팔로잉 -> 언팔로우 -> 다시 팔로잉")
        void followingToUnfollowToFollowing() {
            // given
            Subscription subscription = Subscription.createSubscription(FOLLOWER_ID, FOLLOWEE_ID);
            
            // when & then - 팔로잉 상태
            assertThat(subscription.isFollowing()).isTrue();
            
            // when & then - 언팔로우
            subscription.unfollow();
            assertThat(subscription.isUnFollowed()).isTrue();
            assertThat(subscription.getEndAt()).isNotNull();
            
            // when & then - 다시 팔로잉
            subscription.follow();
            assertThat(subscription.isFollowing()).isTrue();
        }

        @Test
        @DisplayName("팔로잉 -> 차단")
        void followingToBlocked() {
            // given
            Subscription subscription = Subscription.createSubscription(FOLLOWER_ID, FOLLOWEE_ID);
            
            // when & then - 팔로잉 상태
            assertThat(subscription.isFollowing()).isTrue();
            
            // when & then - 차단
            subscription.blockFollowingUser();
            assertThat(subscription.isBlocked()).isTrue();
            assertThat(subscription.getEndAt()).isNotNull();
        }

        @Test
        @DisplayName("언팔로우 -> 차단")
        void unfollowedToBlocked() {
            // given
            Subscription subscription = Subscription.createSubscription(FOLLOWER_ID, FOLLOWEE_ID);
            subscription.unfollow();
            
            // when & then - 언팔로우 상태
            assertThat(subscription.isUnFollowed()).isTrue();
            
            // when & then - 차단
            subscription.blockFollowingUser();
            assertThat(subscription.isBlocked()).isTrue();
        }
    }
}