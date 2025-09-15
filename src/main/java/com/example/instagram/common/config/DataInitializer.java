package com.example.instagram.common.config;

import com.example.instagram.common.security.UserRole;
import com.example.instagram.user.domain.User;
import com.example.instagram.user.domain.UserProfile;
import com.example.instagram.user.domain.UserStatus;
import com.example.instagram.user.infrastructor.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void initData() {
        createAdminUser();
    }

    private void createAdminUser() {
        // 이미 admin 사용자가 존재하는지 확인
        if (userRepository.findByUsername("admin").isPresent()) {
            log.info("Admin user already exists, skipping creation");
            return;
        }

        try {
            // Admin 사용자 생성
            User adminUser = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin"))
                    .profile(UserProfile.createProfile("Administrator", null, null))
                    .status(UserStatus.ACTIVE)
                    .role(UserRole.ADMIN)
                    .build();

            userRepository.save(adminUser);
            log.info("Admin user created successfully with username: admin, password: admin");
            
        } catch (Exception e) {
            log.error("Failed to create admin user: {}", e.getMessage(), e);
        }
    }
}