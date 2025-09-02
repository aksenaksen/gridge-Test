package com.example.instagram.user.application;

import com.example.instagram.user.application.dto.in.UserRegisterCommand;
import com.example.instagram.user.application.dto.out.UserDto;
import com.example.instagram.user.domain.AgreementType;
import com.example.instagram.user.domain.User;
import com.example.instagram.user.domain.UserProfile;
import com.example.instagram.user.domain.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserFinder userFinder;
    
    @Mock
    private UserCommander userCommander;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("유저네임으로 사용자 정보 조회")
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
        given(userFinder.findByUsername(username)).willReturn(user);

        UserDto result = userService.findByUsername(username);

        assertThat(result.username()).isEqualTo(username);
        assertThat(result.name()).isEqualTo("Test Name");
        assertThat(result.status()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    @DisplayName("이름으로 사용자 정보 조회")
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
        given(userFinder.findByName(name)).willReturn(user);

        UserDto result = userService.findByName(name);

        assertThat(result.name()).isEqualTo(name);
        assertThat(result.username()).isEqualTo("testuser");
        assertThat(result.status()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    @DisplayName("등록 날짜로 사용자 정보 조회")
    void findByRegisterDate_Success() {
        LocalDate date = LocalDate.now();
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = start.plusDays(1).minusSeconds(1);
        
        UserProfile profile1 = UserProfile.builder()
                .name("Test Name 1")
                .phoneNumber("01012345678")
                .birthDay(LocalDate.of(1990, 1, 1))
                .build();
        User user1 = User.builder()
                .username("testuser1")
                .password("password")
                .profile(profile1)
                .status(UserStatus.ACTIVE)
                .build();

        UserProfile profile2 = UserProfile.builder()
                .name("Test Name 2")
                .phoneNumber("01087654321")
                .birthDay(LocalDate.of(1985, 5, 15))
                .build();
        User user2 = User.builder()
                .username("testuser2")
                .password("password")
                .profile(profile2)
                .status(UserStatus.ACTIVE)
                .build();

        List<User> users = Arrays.asList(user1, user2);

        given(userFinder.findByRegisterDateTime(start, end)).willReturn(users);

        List<UserDto> result = userService.findByRegisterDate(date);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).username()).isEqualTo("testuser1");
        assertThat(result.get(0).name()).isEqualTo("Test Name 1");
        assertThat(result.get(1).username()).isEqualTo("testuser2");
        assertThat(result.get(1).name()).isEqualTo("Test Name 2");
    }

    @Test
    @DisplayName("사용자 등록")
    void register_Success() {
        UserRegisterCommand command = new UserRegisterCommand(
                "newuser",
                "New User", 
                "01012345678",
                "password123",
                LocalDate.of(1990, 1, 1),
                Arrays.asList(AgreementType.TERMS_OF_SERVICE, AgreementType.DATA_POLICY, AgreementType.LOCATION_BASED_SERVICE)
        );

        userService.register(command);

        verify(userCommander).register(command);
    }

    @Test
    @DisplayName("ID로 사용자 정보 조회")
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
        given(userFinder.findById(userId)).willReturn(user);

        UserDto result = userService.findById(userId);

        assertThat(result.username()).isEqualTo("testuser");
        assertThat(result.name()).isEqualTo("Test Name");
        assertThat(result.status()).isEqualTo(UserStatus.ACTIVE);
    }
}