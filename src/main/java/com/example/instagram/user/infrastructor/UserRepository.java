package com.example.instagram.user.infrastructor;

import com.example.instagram.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
    Optional<User> findByUsername(String username);
    Optional<User> findByProfile_NameOrderByCreatedAt(String name);

    Optional<User> findByProfile_PhoneNumber(String phoneNumber);

    List<User> findByCreatedAtBetweenOrderByCreatedAt(LocalDateTime start, LocalDateTime end);
    boolean existsByUsername(String username);
    List<User> findAllByOrderByCreatedAtDesc();

}
