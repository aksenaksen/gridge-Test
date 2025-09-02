package com.example.instagram.user.application;

import com.example.instagram.user.application.dto.out.UserDto;
import com.example.instagram.user.domain.User;
import com.example.instagram.user.domain.UserProfile;
import com.example.instagram.user.domain.UserStatus;
import com.example.instagram.user.presentation.in.RequestFindAllUserCondition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserServiceConditionTest {

    @Mock
    private UserFinder userFinder;
    
    @Mock
    private UserCommander userCommander;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("조건으로 전체 사용자 조회")
    void findAll_withCondition() {
        RequestFindAllUserCondition condition = new RequestFindAllUserCondition("user1", "김철수", LocalDate.now(), UserStatus.ACTIVE);
        
        UserProfile profile = UserProfile.builder()
                .name("김철수")
                .phoneNumber("01012345678")
                .birthDay(LocalDate.of(1990, 1, 1))
                .build();
        User user = User.builder()
                .username("user1")
                .password("password")
                .profile(profile)
                .status(UserStatus.ACTIVE)
                .build();
                
        given(userFinder.findByCondition(condition)).willReturn(Arrays.asList(user));

        List<UserDto> result = userService.findAll(condition);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).username()).isEqualTo("user1");
        assertThat(result.get(0).name()).isEqualTo("김철수");
        assertThat(result.get(0).status()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    @DisplayName("조건 없이 전체 사용자 조회")
    void findAll_withoutCondition() {
        RequestFindAllUserCondition condition = new RequestFindAllUserCondition(null, null, null, null);
        
        UserProfile profile1 = UserProfile.builder()
                .name("김철수")
                .phoneNumber("01012345678")
                .birthDay(LocalDate.of(1990, 1, 1))
                .build();
        User user1 = User.builder()
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
        User user2 = User.builder()
                .username("user2")
                .password("password")
                .profile(profile2)
                .status(UserStatus.SUSPENDED)
                .build();
                
        given(userFinder.findByCondition(condition)).willReturn(Arrays.asList(user1, user2));

        List<UserDto> result = userService.findAll(condition);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).username()).isEqualTo("user1");
        assertThat(result.get(1).username()).isEqualTo("user2");
    }

    @Test
    @DisplayName("조건에 맞는 사용자가 없을 때")
    void findAll_noResult() {
        RequestFindAllUserCondition condition = new RequestFindAllUserCondition("nonexistent", null, null, null);
        
        given(userFinder.findByCondition(condition)).willReturn(Arrays.asList());

        List<UserDto> result = userService.findAll(condition);

        assertThat(result).isEmpty();
    }
}