package com.example.instagram.batch;

import com.example.instagram.user.domain.AgreementType;
import com.example.instagram.user.domain.User;
import com.example.instagram.user.domain.UserAgreement;
import com.example.instagram.user.domain.UserProfile;
import com.example.instagram.user.domain.UserStatus;
import com.example.instagram.user.infrastructor.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBatchTest
@SpringBootTest

class UserConsentResetConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private Job processResetUserConsentJob;

    private User testUser1;
    private User testUser2;
    private User testUser3;
    @Autowired
    private TransactionTemplate transactionTemplate;

    @Transactional
    @BeforeEach
    void setUp() {
        // JobLauncherTestUtils에 Job 설정
        jobLauncherTestUtils.setJob(processResetUserConsentJob);
        
        // 테스트 사용자 1: TERMS_OF_SERVICE 동의 (과거)
        UserProfile profile1 = UserProfile.builder()
                .name("Test User 1")
                .phoneNumber("01012345678")
                .birthDay(LocalDate.of(1990, 1, 1))
                .build();
        testUser1 = User.builder()
                .username("testuser1")
                .password("password")
                .profile(profile1)
                .status(UserStatus.ACTIVE)
                .build();

        UserAgreement agreement1 = UserAgreement.createAgreement(AgreementType.TERMS_OF_SERVICE);
        // 과거 날짜로 설정을 위한 reflection 사용
        try {
            java.lang.reflect.Field agreedAtField = UserAgreement.class.getDeclaredField("agreedAt");
            agreedAtField.setAccessible(true);
            agreedAtField.set(agreement1, LocalDateTime.now().minusDays(30));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        testUser1.addAgreement(agreement1);

        // 테스트 사용자 2: TERMS_OF_SERVICE 동의 (최근)
        UserProfile profile2 = UserProfile.builder()
                .name("Test User 2")
                .phoneNumber("01087654321")
                .birthDay(LocalDate.of(1985, 5, 15))
                .build();
        testUser2 = User.builder()
                .username("testuser2")
                .password("password")
                .profile(profile2)
                .status(UserStatus.ACTIVE)
                .build();

        UserAgreement agreement2 = UserAgreement.createAgreement(AgreementType.TERMS_OF_SERVICE);
        // 최근 날짜로 설정
        try {
            java.lang.reflect.Field agreedAtField = UserAgreement.class.getDeclaredField("agreedAt");
            agreedAtField.setAccessible(true);
            agreedAtField.set(agreement2, LocalDateTime.now().minusHours(1));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        testUser2.addAgreement(agreement2);

        // 테스트 사용자 3: 비활성 사용자 (처리 대상이 아님)
        UserProfile profile3 = UserProfile.builder()
                .name("Test User 3")
                .phoneNumber("01011111111")
                .birthDay(LocalDate.of(1995, 10, 10))
                .build();
        testUser3 = User.builder()
                .username("testuser3")
                .password("password")
                .profile(profile3)
                .status(UserStatus.ACTIVE)
                .build();

        UserAgreement agreement3 = UserAgreement.createAgreement(AgreementType.TERMS_OF_SERVICE);
        // 과거 날짜로 설정
        try {
            java.lang.reflect.Field agreedAtField = UserAgreement.class.getDeclaredField("agreedAt");
            agreedAtField.setAccessible(true);
            agreedAtField.set(agreement3, LocalDateTime.now().minusYears(30));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        testUser3.addAgreement(agreement3);
        System.out.println("**************"+testUser3.getAgreements().stream().findFirst().get().getAgreedAt());
        userRepository.save(testUser1);
        userRepository.save(testUser2);
        userRepository.save(testUser3);
    }

    @Test
    @DisplayName("배치 실행 성공 테스트")
    void testBatchJobExecution_Success() throws Exception {
        // Given
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(7);
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("cutoffDate", cutoffDate.toString())
                .toJobParameters();

        // When
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        // Then
        assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
        assertThat(jobExecution.getStatus().isUnsuccessful()).isFalse();
    }

    @Test
    @DisplayName("과거에 동의한 TERMS_OF_SERVICE 약관이 철회되는지 테스트")
    void testExpiredTermsOfServiceWithdrawal() throws Exception {
        // Given
        LocalDateTime cutoffDate = LocalDateTime.now().minusYears(1);
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("cutoffDate", cutoffDate.toString())
                .toJobParameters();
        transactionTemplate.execute(status -> {
            User user = userRepository.findById(testUser1.getUserId()).orElseThrow();
            assertThat(user.hasAgreedTo(AgreementType.TERMS_OF_SERVICE)).isTrue();
            return null;
        });



        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
        assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);

        transactionTemplate.execute( status -> {
            // 실행 후 상태 확인
            User userAfter1 = userRepository.findById(testUser1.getUserId()).orElseThrow();
            User userAfter2 = userRepository.findById(testUser2.getUserId()).orElseThrow();
            User userAfter3 = userRepository.findById(testUser3.getUserId()).orElseThrow();

            assertThat(userAfter1.hasAgreedTo(AgreementType.TERMS_OF_SERVICE)).isTrue();
            assertThat(userAfter2.hasAgreedTo(AgreementType.TERMS_OF_SERVICE)).isTrue();
            assertThat(userAfter3.hasAgreedTo(AgreementType.TERMS_OF_SERVICE)).isFalse();
            return null;
        });

    }
}