package com.example.instagram.user.infrastructor;

import com.example.instagram.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByName(String username);
    List<User> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
