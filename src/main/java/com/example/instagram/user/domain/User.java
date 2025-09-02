package com.example.instagram.user.domain;

import com.example.instagram.auth.domain.OAuth2Info;
import com.example.instagram.common.BaseEntity;
import com.example.instagram.common.security.OAuthInfo;
import com.example.instagram.common.security.OAuthProvider;
import com.example.instagram.common.security.UserRole;
import com.example.instagram.user.constant.UserErrorConstant;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;
    
    @Column(name = "password")
    private String password;
    
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(name = "name")),
            @AttributeOverride(name = "phoneNumber", column = @Column(name = "phone_number")),
            @AttributeOverride(name = "birthDay", column = @Column(name = "birth_day"))
    })
    private UserProfile profile;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status;
    
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Embedded
    private OAuthInfo oAuthInfo;

    @Enumerated(EnumType.STRING)
    private UserRole role;


    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserAgreement> agreements = new ArrayList<>();



    public static User createUser(String username, String password, UserProfile profile, PasswordEncoder passwordEncoder){
        User user = new User();

        user.username = username;
        user.password = passwordEncoder.encode(password);
        user.profile = profile;
        user.status = UserStatus.ACTIVE;
        user.role = UserRole.USER;

        return user;
    }

    public static User createFromOAuth(OAuth2Info userInfo, OAuthProvider provider , String oAuthId) {
        User user = new User();

        user.username = oAuthId;
        user.password = null;
        user.status = UserStatus.ACTIVE;
        user.role = UserRole.USER;

        user.profile = UserProfile.createProfile(userInfo.getName(), userInfo.getMobNo(), null);
        user.oAuthInfo = new OAuthInfo(provider, oAuthId);

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
        Assert.state(!this.isActive(), UserErrorConstant.CANNOT_ACTIVATE.getMessage());
        this.status = UserStatus.ACTIVE;
    }

    public void inActivate(){
        Assert.state(!this.isInactive() , UserErrorConstant.CANNOT_DEACTIVATE.getMessage());
        this.status = UserStatus.INACTIVE;
    }

    public void suspend(){
        Assert.state(!this.isSuspended() , UserErrorConstant.CANNOT_SUSPEND.getMessage());
        this.status = UserStatus.SUSPENDED;
    }

    public void changePassword(String newPassword, PasswordEncoder passwordEncoder){
        this.password = passwordEncoder.encode(newPassword);
    }

    public void updateLastLoginAt() {
        this.lastLoginAt = LocalDateTime.now();
    }
    
    public void updateProfile(UserProfile profile) {
        this.profile = profile;
    }

    public void addAllAgreements(List<UserAgreement> agreements) {
        agreements.forEach(this::addAgreement);
    }
    
    public void addAgreement(UserAgreement agreement) {
        this.agreements.add(agreement);
        agreement.setUser(this);
    }
    
    public boolean hasAgreedTo(AgreementType agreementType) {
        return this.agreements.stream()
                .anyMatch(agreement -> agreement.getAgreementType() == agreementType && agreement.isAgreed());
    }

    public void withdrawAgreement(AgreementType agreementType) {
        this.agreements.stream()
                .filter(agreement -> agreement.getAgreementType() == agreementType && agreement.isAgreed())
                .findFirst()
                .ifPresent(UserAgreement::withdraw);
    }
}