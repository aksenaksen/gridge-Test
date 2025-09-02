package com.example.instagram.user.application;

import com.example.instagram.user.application.dto.in.UserRegisterCommand;
import com.example.instagram.user.constant.UserErrorConstant;
import com.example.instagram.user.domain.AgreementType;
import com.example.instagram.user.domain.User;
import com.example.instagram.user.domain.UserAgreement;
import com.example.instagram.user.exception.NotAgreedRequireAgreement;
import com.example.instagram.user.infrastructor.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class UserCommanderTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserCommander userCommander;

    @Test
    @DisplayName("모든 필수 약관에 동의한 사용자 등록 성공")
    void register_Success_WithAllRequiredAgreements() {
        // given
        String username = "testuser";
        String name = "Test Name";
        String phoneNumber = "01012345678";
        String password = "password123";
        String encodedPassword = "encodedPassword123";
        LocalDate birthDay = LocalDate.of(1990, 1, 1);
        List<AgreementType> agreements = Arrays.asList(AgreementType.TERMS_OF_SERVICE, AgreementType.DATA_POLICY, AgreementType.LOCATION_BASED_SERVICE);

        UserRegisterCommand command = new UserRegisterCommand(
                username, name, phoneNumber, password, birthDay, agreements
        );

        given(passwordEncoder.encode(password)).willReturn(encodedPassword);

        // when
        userCommander.register(command);

        // then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getUsername()).isEqualTo(username);
        assertThat(savedUser.getPassword()).isEqualTo(encodedPassword);
        assertThat(savedUser.getProfile().getName()).isEqualTo(name);
        assertThat(savedUser.getProfile().getPhoneNumber()).isEqualTo(phoneNumber);
        assertThat(savedUser.getProfile().getBirthDay()).isEqualTo(birthDay);
        assertThat(savedUser.getAgreements()).hasSize(3);
        assertThat(savedUser.hasAgreedTo(AgreementType.TERMS_OF_SERVICE)).isTrue();
        assertThat(savedUser.hasAgreedTo(AgreementType.DATA_POLICY)).isTrue();
        assertThat(savedUser.hasAgreedTo(AgreementType.LOCATION_BASED_SERVICE)).isTrue();
    }

    @Test
    @DisplayName("필수 약관 중 일부가 누락된 경우 등록 실패")
    void register_Fail_WhenMissingRequiredAgreements() {
        // given
        String username = "testuser";
        String name = "Test Name";
        String phoneNumber = "01012345678";
        String password = "password123";
        LocalDate birthDay = LocalDate.of(1990, 1, 1);
        List<AgreementType> agreements = Arrays.asList(AgreementType.TERMS_OF_SERVICE);

        UserRegisterCommand command = new UserRegisterCommand(
                username, name, phoneNumber, password, birthDay, agreements
        );

        // when & then
        assertThatThrownBy(() -> userCommander.register(command))
                .isInstanceOf(NotAgreedRequireAgreement.class)
                .hasMessage(UserErrorConstant.NOT_AGREED.getMessage())
                .extracting("notAgreedTypes")
                .satisfies(notAgreedTypes -> {
                    @SuppressWarnings("unchecked")
                    List<AgreementType> types = (List<AgreementType>) notAgreedTypes;
                    assertThat(types).contains(AgreementType.DATA_POLICY, AgreementType.LOCATION_BASED_SERVICE);
                    assertThat(types).doesNotContain(AgreementType.TERMS_OF_SERVICE);
                });
    }

    @Test
    @DisplayName("빈 약관 리스트로 등록 시도 시 실패")
    void register_Fail_WhenEmptyAgreements() {
        // given
        String username = "testuser";
        String name = "Test Name";
        String phoneNumber = "01012345678";
        String password = "password123";
        LocalDate birthDay = LocalDate.of(1990, 1, 1);
        List<AgreementType> agreements = Collections.emptyList();

        UserRegisterCommand command = new UserRegisterCommand(
                username, name, phoneNumber, password, birthDay, agreements
        );

        // when & then
        assertThatThrownBy(() -> userCommander.register(command))
                .isInstanceOf(NotAgreedRequireAgreement.class)
                .hasMessage(UserErrorConstant.NOT_AGREED.getMessage())
                .extracting("notAgreedTypes")
                .satisfies(notAgreedTypes -> {
                    @SuppressWarnings("unchecked")
                    List<AgreementType> types = (List<AgreementType>) notAgreedTypes;
                    assertThat(types).containsExactlyInAnyOrder(
                            AgreementType.TERMS_OF_SERVICE,
                            AgreementType.DATA_POLICY,
                            AgreementType.LOCATION_BASED_SERVICE
                    );
                });
    }

    @Test
    @DisplayName("패스워드가 올바르게 인코딩되는지 확인")
    void register_Success_PasswordEncoding() {
        // given
        String username = "testuser";
        String name = "Test Name";
        String phoneNumber = "01012345678";
        String password = "plainPassword";
        String encodedPassword = "encodedPassword123";
        LocalDate birthDay = LocalDate.of(1990, 1, 1);
        List<AgreementType> agreements = Arrays.asList(AgreementType.TERMS_OF_SERVICE, AgreementType.DATA_POLICY, AgreementType.LOCATION_BASED_SERVICE);

        UserRegisterCommand command = new UserRegisterCommand(
                username, name, phoneNumber, password, birthDay, agreements
        );

        given(passwordEncoder.encode(password)).willReturn(encodedPassword);

        // when
        userCommander.register(command);

        // then
        verify(passwordEncoder).encode(password);
        
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getPassword()).isEqualTo(encodedPassword);
        assertThat(savedUser.getPassword()).isNotEqualTo(password);
    }

    @Test
    @DisplayName("사용자 약관 정보가 올바르게 설정되는지 확인")
    void register_Success_AgreementsSetProperly() {
        // given
        String username = "testuser";
        String name = "Test Name";
        String phoneNumber = "01012345678";
        String password = "password123";
        String encodedPassword = "encodedPassword123";
        LocalDate birthDay = LocalDate.of(1990, 1, 1);
        List<AgreementType> agreements = Arrays.asList(AgreementType.TERMS_OF_SERVICE, AgreementType.DATA_POLICY, AgreementType.LOCATION_BASED_SERVICE);

        UserRegisterCommand command = new UserRegisterCommand(
                username, name, phoneNumber, password, birthDay, agreements
        );

        given(passwordEncoder.encode(password)).willReturn(encodedPassword);

        // when
        userCommander.register(command);

        // then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        List<UserAgreement> userAgreements = savedUser.getAgreements();
        
        assertThat(userAgreements).hasSize(3);
        assertThat(userAgreements).allSatisfy(agreement -> {
            assertThat(agreement.isAgreed()).isTrue();
            assertThat(agreement.getAgreedAt()).isNotNull();
            assertThat(agreement.getUser()).isEqualTo(savedUser);
        });
        
        assertThat(userAgreements)
                .extracting(UserAgreement::getAgreementType)
                .containsExactlyInAnyOrder(
                        AgreementType.TERMS_OF_SERVICE,
                        AgreementType.DATA_POLICY,
                        AgreementType.LOCATION_BASED_SERVICE
                );
    }

    @Test
    @DisplayName("사용자 프로필 정보가 올바르게 설정되는지 확인")
    void register_Success_ProfileSetProperly() {
        // given
        String username = "testuser";
        String name = "홍길동";
        String phoneNumber = "01087654321";
        String password = "password123";
        String encodedPassword = "encodedPassword123";
        LocalDate birthDay = LocalDate.of(1985, 12, 25);
        List<AgreementType> agreements = Arrays.asList(AgreementType.TERMS_OF_SERVICE, AgreementType.DATA_POLICY, AgreementType.LOCATION_BASED_SERVICE);

        UserRegisterCommand command = new UserRegisterCommand(
                username, name, phoneNumber, password, birthDay, agreements
        );

        given(passwordEncoder.encode(password)).willReturn(encodedPassword);

        // when
        userCommander.register(command);

        // then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getProfile()).isNotNull();
        assertThat(savedUser.getProfile().getName()).isEqualTo(name);
        assertThat(savedUser.getProfile().getPhoneNumber()).isEqualTo(phoneNumber);
        assertThat(savedUser.getProfile().getBirthDay()).isEqualTo(birthDay);
    }

    @Test
    @DisplayName("사용자 상태가 ACTIVE로 설정되는지 확인")
    void register_Success_UserStatusIsActive() {
        // given
        String username = "testuser";
        String name = "Test Name";
        String phoneNumber = "01012345678";
        String password = "password123";
        String encodedPassword = "encodedPassword123";
        LocalDate birthDay = LocalDate.of(1990, 1, 1);
        List<AgreementType> agreements = Arrays.asList(AgreementType.TERMS_OF_SERVICE, AgreementType.DATA_POLICY, AgreementType.LOCATION_BASED_SERVICE);

        UserRegisterCommand command = new UserRegisterCommand(
                username, name, phoneNumber, password, birthDay, agreements
        );

        given(passwordEncoder.encode(password)).willReturn(encodedPassword);

        // when
        userCommander.register(command);

        // then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.isActive()).isTrue();
        assertThat(savedUser.isSuspended()).isFalse();
        assertThat(savedUser.isInactive()).isFalse();
    }

    @Test
    @DisplayName("UserRepository.save가 정확히 한 번 호출되는지 확인")
    void register_Success_RepositorySaveCalledOnce() {
        // given
        String username = "testuser";
        String name = "Test Name";
        String phoneNumber = "01012345678";
        String password = "password123";
        String encodedPassword = "encodedPassword123";
        LocalDate birthDay = LocalDate.of(1990, 1, 1);
        List<AgreementType> agreements = Arrays.asList(AgreementType.TERMS_OF_SERVICE, AgreementType.DATA_POLICY, AgreementType.LOCATION_BASED_SERVICE);

        UserRegisterCommand command = new UserRegisterCommand(
                username, name, phoneNumber, password, birthDay, agreements
        );

        given(passwordEncoder.encode(password)).willReturn(encodedPassword);

        // when
        userCommander.register(command);

        // then
        verify(userRepository).save(any(User.class));
    }
}