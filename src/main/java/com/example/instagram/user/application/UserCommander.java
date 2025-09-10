package com.example.instagram.user.application;

import com.example.instagram.user.application.dto.in.UserDeleteCommand;
import com.example.instagram.user.application.dto.in.UserRegisterCommand;
import com.example.instagram.user.application.dto.in.UserSuspendCommand;
import com.example.instagram.user.application.dto.in.UserUpdatePasswordCommand;
import com.example.instagram.user.domain.AgreementType;
import com.example.instagram.user.domain.User;
import com.example.instagram.user.domain.UserAgreement;
import com.example.instagram.user.exception.UserAlreadyExistException;
import com.example.instagram.user.exception.UserNotFoundException;
import com.example.instagram.user.infrastructor.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserCommander {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void register(UserRegisterCommand command){

        AgreementType.validateRequiredAgreements(command.agreements());
        
        User user = User.createUser(command.username(), command.password() , command.toProfile() , passwordEncoder);

        List<UserAgreement> agreements = command.agreements().stream()
                        .map(UserAgreement::createAgreement)
                        .toList();

        user.addAllAgreements(agreements);

        try {
            userRepository.save(user);
        }
        catch (DataIntegrityViolationException e){
            throw new UserAlreadyExistException();
        }
    }

    public void updatePassword(UserUpdatePasswordCommand command){
        User user = null;
        if(command.phoneNumber() != null){
            user = userRepository.findByProfile_PhoneNumber(command.phoneNumber())
                    .orElseThrow(UserNotFoundException::new);
        }
        else{
            user = userRepository.findByUsername(command.username())
                    .orElseThrow(UserNotFoundException::new);
        }
        user.changePassword(command.password(), passwordEncoder);
    }

    public void delete(UserSuspendCommand command){
        User user = userRepository.findById(command.userId())
                .orElseThrow(UserNotFoundException::new);
        user.suspend();
        userRepository.save(user);
    }

    public void delete(UserDeleteCommand command){
        User user = userRepository.findById(command.userId())
                .orElseThrow(UserNotFoundException::new);
        user.inActivate();
        userRepository.save(user);
    }
}
