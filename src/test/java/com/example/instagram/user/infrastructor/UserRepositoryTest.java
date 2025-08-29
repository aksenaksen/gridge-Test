package com.example.instagram.user.infrastructor;

import com.example.instagram.user.domain.User;
import com.example.instagram.user.domain.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EnableJpaAuditing
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("유저네임으로 사용자 찾기")
    void findByUsername() {
        User user = User.builder()
                .username("testuser")
                .name("Test Name")
                .password("password")
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByUsername("testuser");

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
        assertThat(foundUser.get().getName()).isEqualTo("Test Name");
    }

    @Test
    @DisplayName("존재하지 않는 유저네임으로 조회")
    void findByUsername_NotFound() {
        Optional<User> foundUser = userRepository.findByUsername("nonexistent");

        assertThat(foundUser).isEmpty();
    }

    @Test
    @DisplayName("이름으로 사용자 찾기")
    void findByName() {
        User user = User.builder()
                .username("testuser")
                .name("Test Name")
                .password("password")
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByName("Test Name");

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo("Test Name");
        assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("존재하지 않는 이름으로 조회")
    void findByName_NotFound() {
        Optional<User> foundUser = userRepository.findByName("Nonexistent Name");

        assertThat(foundUser).isEmpty();
    }

    @Test
    @DisplayName("생성 기간으로 사용자 목록 찾기")
    void findByCreatedAtBetween() {
        User user1 = User.builder()
                .username("testuser1")
                .name("Test Name 1")
                .password("password")
                .status(UserStatus.ACTIVE)
                .build();

        User user2 = User.builder()
                .username("testuser2")
                .name("Test Name 2")
                .password("password")
                .status(UserStatus.ACTIVE)
                .build();

        ReflectionTestUtils.setField(user1, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(user2, "createdAt", LocalDateTime.now().plusDays(2));

        userRepository.save(user1);
        userRepository.save(user2);

        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(3);

        List<User> foundUsers = userRepository.findByCreatedAtBetween(start, end);

        assertThat(foundUsers).hasSize(2);
        assertThat(foundUsers).extracting(User::getUsername).contains("testuser1", "testuser2");
    }

    @Test
    @DisplayName("생성 기간에 해당하는 사용자가 없을 때")
    void findByCreatedAtBetween_NotFound() {
        LocalDateTime start = LocalDateTime.now().minusDays(10);
        LocalDateTime end = LocalDateTime.now().minusDays(5);

        List<User> foundUsers = userRepository.findByCreatedAtBetween(start, end);

        assertThat(foundUsers).isEmpty();
    }
}