package com.example.instagram.auth.application;

import com.example.instagram.auth.domain.CustomUserDetails;
import com.example.instagram.auth.exception.NotActiveUserException;
import com.example.instagram.user.domain.User;
import com.example.instagram.user.infrastructor.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.instagram.auth.constant.AuthMessageConstant.NOT_FOUND_USERNAME;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    @Override
    @Transactional(readOnly = true)
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(NOT_FOUND_USERNAME + username));
        if(!user.isActive()) throw new NotActiveUserException();

        return new CustomUserDetails(user);
    }

}