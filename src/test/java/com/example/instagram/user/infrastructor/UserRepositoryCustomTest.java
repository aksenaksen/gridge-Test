package com.example.instagram.user.infrastructor;

import com.example.instagram.user.domain.User;
import com.example.instagram.user.domain.UserProfile;
import com.example.instagram.user.domain.UserStatus;
import com.example.instagram.user.presentation.in.RequestFindAllUserCondition;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(UserRepositoryImpl.class)
class UserRepositoryCustomTest {

    @Autowired
    private UserRepository userRepository;
    
    @TestConfiguration
    static class TestConfig {
        @Bean
        JPAQueryFactory jpaQueryFactory(EntityManager entityManager) {
            return new JPAQueryFactory(entityManager);
        }
    }

    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    void setUp() {
        UserProfile profile1 = UserProfile.builder()
                .name("김철수")
                .phoneNumber("01012345678")
                .birthDay(LocalDate.of(1990, 1, 1))
                .build();
        user1 = User.builder()
                .username("user1")
                .password("password")
                .profile(profile1)
                .status(UserStatus.ACTIVE)
                .build();

        UserProfile profile2 = UserProfile.builder()
                .name("이영희")
                .phoneNumber("01087654321")
                .birthDay(LocalDate.of(1985, 5, 15))
                .build();
        user2 = User.builder()
                .username("user2")
                .password("password")
                .profile(profile2)
                .status(UserStatus.SUSPENDED)
                .build();

        UserProfile profile3 = UserProfile.builder()
                .name("박민수")
                .phoneNumber("01011111111")
                .birthDay(LocalDate.of(1995, 10, 10))
                .build();
        user3 = User.builder()
                .username("testuser")
                .password("password")
                .profile(profile3)
                .status(UserStatus.ACTIVE)
                .build();

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
    }

    @Test
    @DisplayName("조건 없이 전체 사용자 조회")
    void findByCondition_noCondition() {
        RequestFindAllUserCondition condition = new RequestFindAllUserCondition(null, null, null, null);

        List<User> result = userRepository.findByCondition(condition);

        assertThat(result).hasSize(3);
    }

    @Test
    @DisplayName("유저명으로 조건 검색")
    void findByCondition_byUsername() {
        RequestFindAllUserCondition condition = new RequestFindAllUserCondition("user1", null, null, null);

        List<User> result = userRepository.findByCondition(condition);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("user1");
    }

    @Test
    @DisplayName("이름으로 조건 검색")
    void findByCondition_byName() {
        RequestFindAllUserCondition condition = new RequestFindAllUserCondition(null, "김철수", null, null);

        List<User> result = userRepository.findByCondition(condition);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getProfile().getName()).isEqualTo("김철수");
    }

    @Test
    @DisplayName("상태로 조건 검색")
    void findByCondition_byStatus() {
        RequestFindAllUserCondition condition = new RequestFindAllUserCondition(null, null, null, UserStatus.ACTIVE);

        List<User> result = userRepository.findByCondition(condition);

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(user -> user.getStatus() == UserStatus.ACTIVE);
    }

    @Test
    @DisplayName("생성날짜로 조건 검색")
    void findByCondition_byDate() {
        LocalDate today = LocalDate.now();
        RequestFindAllUserCondition condition = new RequestFindAllUserCondition(null, null, today, null);

        List<User> result = userRepository.findByCondition(condition);

        assertThat(result).hasSize(3);
        assertThat(result).allMatch(user -> user.getCreatedAt().toLocalDate().equals(today));
    }

    @Test
    @DisplayName("복합 조건 검색")
    void findByCondition_multipleConditions() {
        RequestFindAllUserCondition condition = new RequestFindAllUserCondition("user1", "김철수", null, UserStatus.ACTIVE);

        List<User> result = userRepository.findByCondition(condition);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("user1");
        assertThat(result.get(0).getProfile().getName()).isEqualTo("김철수");
        assertThat(result.get(0).getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    @DisplayName("조건에 맞는 사용자가 없을 때")
    void findByCondition_noMatch() {
        RequestFindAllUserCondition condition = new RequestFindAllUserCondition("nonexistent", null, null, null);

        List<User> result = userRepository.findByCondition(condition);

        assertThat(result).isEmpty();
    }
}