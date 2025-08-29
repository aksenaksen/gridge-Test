package com.example.instagram.user.domain;

import com.example.instagram.common.BaseEntity;
import com.example.instagram.user.constant.UserMessage;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "password", nullable = false)
    private String password;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status;
    
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    
    @Builder
    public User(String username, String name, String password, UserStatus status) {
        this.username = username;
        this.name = name;
        this.password = password;
        this.status = status;
    }

    public static User createUser(String username, String name, String password , PasswordEncoder passwordEncoder){
        User user = new User();
        user.username = username;
        user.name = name;
        user.password = passwordEncoder.encode(password);
        return user;
    }

    public boolean isActive(){
        return this.status == UserStatus.ACTIVE;
    }

    public boolean isSuspended(){
        return this.status == UserStatus.SUSPENDED;
    }

    public boolean isInactive(){
        return this.status == UserStatus.INACTIVE;
    }

    public void activate(){
        Assert.state(!this.isActive(), UserMessage.CANNOT_ACTIVATE.getMessage());
        this.status = UserStatus.ACTIVE;
    }

    public void inActivate(){
        Assert.state(!this.isInactive() , UserMessage.CANNOT_DEACTIVATE.getMessage());
        this.status = UserStatus.INACTIVE;
    }

    public void suspend(){
        Assert.state(!this.isSuspended() , UserMessage.CANNOT_DEACTIVATE.getMessage());
        this.status = UserStatus.SUSPENDED;
    }

    public void updateLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }
    

}