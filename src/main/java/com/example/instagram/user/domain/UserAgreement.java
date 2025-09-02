package com.example.instagram.user.domain;

import com.example.instagram.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_agreements")
public class UserAgreement extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "agreement_id")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "agreement_type", nullable = false)
    private AgreementType agreementType;
    
    @Column(name = "is_agreed", nullable = false)
    private boolean isAgreed;
    
    @Column(name = "agreed_at")
    private LocalDateTime agreedAt;
    
    @Builder
    public UserAgreement(User user, AgreementType agreementType, boolean isAgreed, 
                        LocalDateTime agreedAt) {
        this.user = user;
        this.agreementType = agreementType;
        this.isAgreed = isAgreed;
        this.agreedAt = agreedAt;
    }
    
    public static UserAgreement createAgreement(AgreementType agreementType) {
        return UserAgreement.builder()
                .agreementType(agreementType)
                .isAgreed(true)
                .agreedAt(LocalDateTime.now())
                .build();
    }


    public void withdraw() {
        this.isAgreed = false;
        this.agreedAt = null;
    }
    
    public void agree() {
        this.isAgreed = true;
        this.agreedAt = LocalDateTime.now();
    }
    
    public void setUser(User user) {
        this.user = user;
    }

}