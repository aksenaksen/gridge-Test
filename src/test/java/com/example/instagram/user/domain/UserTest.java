package com.example.instagram.user.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    private User createActiveUser() {
        return User.builder()
                .username("testuser")
                .name("Test Name")
                .password("password")
                .status(UserStatus.ACTIVE)
                .build();
    }

    private User createSuspendedUser() {
        return User.builder()
                .username("testuser")
                .name("Test Name")
                .password("password")
                .status(UserStatus.SUSPENDED)
                .build();
    }

    private User createInactiveUser() {
        return User.builder()
                .username("testuser")
                .name("Test Name")
                .password("password")
                .status(UserStatus.INACTIVE)
                .build();
    }

    @Test
    @DisplayName("사용자 생성")
    void createUser_Success() {
        String username = "testuser";
        String name = "Test Name";
        String password = "password123";
        UserStatus status = UserStatus.ACTIVE;

        User user = User.builder()
                .username(username)
                .name(name)
                .password(password)
                .status(status)
                .build();

        assertThat(user.getUsername()).isEqualTo(username);
        assertThat(user.getName()).isEqualTo(name);
        assertThat(user.getPassword()).isEqualTo(password);
        assertThat(user.getStatus()).isEqualTo(status);
    }

    @Test
    @DisplayName("정적 팩토리 메서드로 사용자를 생성할 수 있다")
    void createUserWithStaticFactory_Success() {
        String username = "testuser";
        String name = "Test Name";
        String password = "password123";
        String encodedPassword = "encodedPassword123";

        given(passwordEncoder.encode(password)).willReturn(encodedPassword);

        User user = User.createUser(username, name, password, passwordEncoder);

        assertThat(user.getUsername()).isEqualTo(username);
        assertThat(user.getName()).isEqualTo(name);
        assertThat(user.getPassword()).isEqualTo(encodedPassword);
    }

    @Test
    @DisplayName("활성 상태 체크")
    void isActive_WhenUserIsActive() {
        User user = createActiveUser();

        assertThat(user.isActive()).isTrue();
        assertThat(user.isSuspended()).isFalse();
        assertThat(user.isInactive()).isFalse();
    }

    @Test
    @DisplayName("중지 상태 체크")
    void isSuspended_WhenUserIsSuspended() {
        User user = createSuspendedUser();

        assertThat(user.isSuspended()).isTrue();
        assertThat(user.isActive()).isFalse();
        assertThat(user.isInactive()).isFalse();
    }

    @Test
    @DisplayName("비활성 상태 체크")
    void isInactive_WhenUserIsInactive() {
        User user = createInactiveUser();

        assertThat(user.isInactive()).isTrue();
        assertThat(user.isActive()).isFalse();
        assertThat(user.isSuspended()).isFalse();
    }

    @Test
    @DisplayName("로그인 시간 업데이트 테스트")
    void updateLastLoginAt_Success() {
        User user = createActiveUser();
        LocalDateTime loginTime = LocalDateTime.now();

        user.updateLastLoginAt(loginTime);

        assertThat(user.getLastLoginAt()).isEqualTo(loginTime);
    }

    @Test
    @DisplayName("activate 함수 테스트")
    void activate() {
        User activeUser = createActiveUser();
        User inActiveUser = createInactiveUser();
        User suspendedUser = createSuspendedUser();

        assertThatThrownBy(activeUser::activate)
                .isInstanceOf(IllegalStateException.class);

        inActiveUser.activate();
        suspendedUser.activate();

        assertThat(inActiveUser.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(suspendedUser.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    @DisplayName("inActivate 함수 테스트")
    void inActivate() {
        User activeUser = createActiveUser();
        User inActiveUser = createInactiveUser();
        User suspendedUser = createSuspendedUser();

        assertThatThrownBy(inActiveUser::inActivate)
                .isInstanceOf(IllegalStateException.class);

        activeUser.inActivate();
        suspendedUser.inActivate();

        assertThat(activeUser.getStatus()).isEqualTo(UserStatus.INACTIVE);
        assertThat(suspendedUser.getStatus()).isEqualTo(UserStatus.INACTIVE);
    }

    @Test
    @DisplayName("suspend 함수 테스트")
    void suspend() {

        User activeUser = createActiveUser();
        User inActiveUser = createInactiveUser();
        User suspendedUser = createSuspendedUser();

        assertThatThrownBy(suspendedUser::suspend)
                .isInstanceOf(IllegalStateException.class);

        inActiveUser.suspend();
        activeUser.suspend();

        assertThat(inActiveUser.getStatus()).isEqualTo(UserStatus.SUSPENDED);
        assertThat(activeUser.getStatus()).isEqualTo(UserStatus.SUSPENDED);
    }
}