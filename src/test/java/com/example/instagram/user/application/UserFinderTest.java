package com.example.instagram.user.application;

import com.example.instagram.user.domain.User;
import com.example.instagram.user.domain.UserProfile;
import com.example.instagram.user.domain.UserStatus;
import com.example.instagram.user.exception.UserNotFoundException;
import com.example.instagram.user.infrastructor.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserFinderTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserFinder userFinder;


    @Test
    @DisplayName("유저네임으로 사용자 찾기")
    void findByUsername_Success() {
        String username = "testuser";
        UserProfile profile = UserProfile.builder()
                .name("Test Name")
                .phoneNumber("01012345678")
                .birthDay(LocalDate.of(1990, 1, 1))
                .build();
        User user = User.builder()
                .username(username)
                .password("password")
                .profile(profile)
                .status(UserStatus.ACTIVE)
                .build();
        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));

        User foundUser = userFinder.findByUsername(username);

        assertThat(foundUser.getUsername()).isEqualTo(username);
        assertThat(foundUser.getProfile().getName()).isEqualTo("Test Name");
    }

    @Test
    @DisplayName("존재하지 않는 유저네임으로 조회시 예외 발생")
    void findByUsername_NotFound() {
        String username = "nonexistent";
        given(userRepository.findByUsername(username)).willReturn(Optional.empty());

        assertThatThrownBy(() -> userFinder.findByUsername(username))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("이름으로 사용자 찾기")
    void findByName_Success() {
        String name = "Test Name";
        UserProfile profile = UserProfile.builder()
                .name(name)
                .phoneNumber("01012345678")
                .birthDay(LocalDate.of(1990, 1, 1))
                .build();
        User user = User.builder()
                .username("testuser")
                .password("password")
                .profile(profile)
                .status(UserStatus.ACTIVE)
                .build();
        given(userRepository.findByProfile_NameOrderByCreatedAt(name)).willReturn(Optional.of(user));

        User foundUser = userFinder.findByName(name);

        assertThat(foundUser.getProfile().getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("존재하지 않는 이름으로 조회시 예외 발생")
    void findByName_NotFound() {
        String name = "Nonexistent Name";
        given(userRepository.findByProfile_NameOrderByCreatedAt(name)).willReturn(Optional.empty());

        assertThatThrownBy(() -> userFinder.findByName(name))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("등록 기간으로 사용자 찾기")
    void findByRegisterDateTime_Success() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        UserProfile profile1 = UserProfile.builder()
                .name("Test Name")
                .phoneNumber("01012345678")
                .birthDay(LocalDate.of(1990, 1, 1))
                .build();
        User user = User.builder()
                .username("testuser")
                .password("password")
                .profile(profile1)
                .status(UserStatus.ACTIVE)
                .build();

        UserProfile profile2 = UserProfile.builder()
                .name("Test Name2")
                .phoneNumber("01087654321")
                .birthDay(LocalDate.of(1985, 5, 15))
                .build();
        User user2 = User.builder()
                .username("testuser2")
                .password("password")
                .profile(profile2)
                .status(UserStatus.ACTIVE)
                .build();

        ReflectionTestUtils.setField(user, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(user2, "createdAt", LocalDateTime.now().plusDays(2));

        List<User> users = Arrays.asList(user, user2);

        given(userRepository.findByCreatedAtBetweenOrderByCreatedAt(start, end)).willReturn(users);

        List<User> foundUser = userFinder.findByRegisterDateTime(start, end);
        assertThat(foundUser.size()).isEqualTo(2);
        assertThat(foundUser).isNotNull();
    }


    @Test
    @DisplayName("ID로 사용자 찾기")
    void findById_Success() {
        Long userId = 1L;
        UserProfile profile = UserProfile.builder()
                .name("Test Name")
                .phoneNumber("01012345678")
                .birthDay(LocalDate.of(1990, 1, 1))
                .build();
        User user = User.builder()
                .username("testuser")
                .password("password")
                .profile(profile)
                .status(UserStatus.ACTIVE)
                .build();
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        User foundUser = userFinder.findById(userId);

        assertThat(foundUser).isNotNull();
    }

    @Test
    @DisplayName("존재하지 않는 ID로 조회시 예외 발생")
    void findById_NotFound() {
        Long userId = 999L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> userFinder.findById(userId))
                .isInstanceOf(UserNotFoundException.class);
    }
}