package com.example.instagram.user.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    private User createActiveUser() {
        UserProfile profile = UserProfile.builder()
                .name("Test Name")
                .phoneNumber("01012345678")
                .birthDay(LocalDate.of(1990, 1, 1))
                .build();
        return User.builder()
                .username("testuser")
                .password("password")
                .profile(profile)
                .status(UserStatus.ACTIVE)
                .build();
    }

    private User createSuspendedUser() {
        UserProfile profile = UserProfile.builder()
                .name("Test Name")
                .phoneNumber("01012345678")
                .birthDay(LocalDate.of(1990, 1, 1))
                .build();
        return User.builder()
                .username("testuser")
                .password("password")
                .profile(profile)
                .status(UserStatus.SUSPENDED)
                .build();
    }

    private User createInactiveUser() {
        UserProfile profile = UserProfile.builder()
                .name("Test Name")
                .phoneNumber("01012345678")
                .birthDay(LocalDate.of(1990, 1, 1))
                .build();
        return User.builder()
                .username("testuser")
                .password("password")
                .profile(profile)
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

        UserProfile profile = UserProfile.builder()
                .name(name)
                .phoneNumber("01012345678")
                .birthDay(LocalDate.of(1990, 1, 1))
                .build();
        User user = User.builder()
                .username(username)
                .password(password)
                .profile(profile)
                .status(status)
                .build();

        assertThat(user.getUsername()).isEqualTo(username);
        assertThat(user.getProfile().getName()).isEqualTo(name);
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

        UserProfile profile = UserProfile.builder()
                .name(name)
                .phoneNumber("01012345678")
                .birthDay(LocalDate.of(1990, 1, 1))
                .build();
        User user = User.createUser(username, password, profile, passwordEncoder);

        assertThat(user.getUsername()).isEqualTo(username);
        assertThat(user.getProfile().getName()).isEqualTo(name);
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
        LocalDateTime beforeUpdate = LocalDateTime.now().minusSeconds(1);

        user.updateLastLoginAt();

        assertThat(user.getLastLoginAt()).isAfter(beforeUpdate);
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

    @Test
    @DisplayName("UserAgreement 추가 테스트")
    void addAgreement_Success() {
        User user = createActiveUser();
        UserAgreement agreement = UserAgreement.builder()
                .agreementType(AgreementType.TERMS_OF_SERVICE)
                .isAgreed(true)
                .build();

        user.addAgreement(agreement);

        assertThat(user.getAgreements()).hasSize(1);
        assertThat(user.getAgreements().get(0).getAgreementType()).isEqualTo(AgreementType.TERMS_OF_SERVICE);
        assertThat(agreement.getUser()).isEqualTo(user);
    }

    @Test
    @DisplayName("hasAgreedTo 테스트")
    void hasAgreedTo_Success() {
        User user = createActiveUser();
        UserAgreement serviceTerms = UserAgreement.builder()
                .agreementType(AgreementType.TERMS_OF_SERVICE)
                .isAgreed(true)
                .build();
        UserAgreement privacyPolicy = UserAgreement.builder()
                .agreementType(AgreementType.DATA_POLICY)
                .isAgreed(false)
                .build();

        user.addAgreement(serviceTerms);
        user.addAgreement(privacyPolicy);

        assertThat(user.hasAgreedTo(AgreementType.TERMS_OF_SERVICE)).isTrue();
        assertThat(user.hasAgreedTo(AgreementType.DATA_POLICY)).isFalse();
        assertThat(user.hasAgreedTo(AgreementType.LOCATION_BASED_SERVICE)).isFalse();
    }

    @Test
    @DisplayName("프로필 업데이트 테스트")
    void updateProfile_Success() {
        User user = createActiveUser();
        UserProfile newProfile = UserProfile.builder()
                .name("Updated Name")
                .phoneNumber("01087654321")
                .birthDay(LocalDate.of(1995, 5, 5))
                .build();

        user.updateProfile(newProfile);

        assertThat(user.getProfile().getName()).isEqualTo("Updated Name");
        assertThat(user.getProfile().getPhoneNumber()).isEqualTo("01087654321");
        assertThat(user.getProfile().getBirthDay()).isEqualTo(LocalDate.of(1995, 5, 5));
    }
}