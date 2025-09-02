package com.example.instagram.auth.application;

import com.example.instagram.auth.domain.CustomUserDetails;
import com.example.instagram.auth.domain.NaverUserInfo;
import com.example.instagram.auth.domain.OAuth2Info;
import com.example.instagram.auth.exception.NotActiveUserException;
import com.example.instagram.common.security.OAuthProvider;
import com.example.instagram.user.domain.User;
import com.example.instagram.user.infrastructor.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;

import static com.example.instagram.auth.constant.AuthMessageConstant.NOT_SUPPORTED_PROVIDER_MESSAGE;
import static com.example.instagram.auth.constant.AuthMessageConstant.OAUTH_PROVIDER_NAVER;

@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2Info userInfo = switch (registrationId) {
            case OAUTH_PROVIDER_NAVER -> new NaverUserInfo((Map<String, Object>) oAuth2User.getAttributes().get("response"));
            default -> throw new OAuth2AuthenticationException(NOT_SUPPORTED_PROVIDER_MESSAGE+ registrationId);
        };

        User user = getUser(userInfo, registrationId, userInfo.getEmail());
        userRepository.save(user);

        return new CustomUserDetails(user, oAuth2User.getAttributes());
    }

    private User getUser(OAuth2Info userInfo, String registrationId , String oAuthId) {
        return userRepository.findByUsername(userInfo.getEmail())
                .map(existingUser -> {
                    if (!existingUser.isActive()) {
                        throw new NotActiveUserException();
                    }
                    return existingUser;
                })
                .orElseGet(() -> {
                    OAuthProvider provider = OAuthProvider.from(registrationId);
                    User newUser = User.createFromOAuth(userInfo, provider, oAuthId);
                    LocalDate birthDate = parseBirthday(userInfo);

                    newUser.getProfile().updateBirthDay(birthDate);
                    return newUser;
                });
    }

    private LocalDate parseBirthday(OAuth2Info userInfo){
        String birthday = userInfo.getBirthYear() + "-" +userInfo.getBirthday();
        return LocalDate.parse(birthday);
    }
}