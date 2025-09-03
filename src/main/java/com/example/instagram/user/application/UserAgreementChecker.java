package com.example.instagram.user.application;

import com.example.instagram.user.domain.AgreementType;
import com.example.instagram.user.domain.User;
import com.example.instagram.user.domain.UserAgreement;
import com.example.instagram.user.exception.UserNotFoundException;
import com.example.instagram.user.infrastructor.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserAgreementChecker {

    private final UserRepository userRepository;

    public void checkRequiredAgreement(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        List<AgreementType> agreementTypes = user.getAgreements().stream()
                        .map(UserAgreement::getAgreementType)
                        .toList();

        AgreementType.validateRequiredAgreements(agreementTypes);
    }

}
