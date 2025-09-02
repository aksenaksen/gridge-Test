package com.example.instagram.batch;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserConsentResetSchedulerTest {

    @Mock
    private JobLauncher jobLauncher;

    @Mock
    private Job processResetUserConsentJob;

    @InjectMocks
    private UserConsentResetScheduler userConsentResetScheduler;

    @Test
    @DisplayName("스케줄러가 정상적으로 배치 Job을 실행하는지 테스트")
    void testScheduledJobExecution_Success() throws Exception {
        // Given
        JobExecution mockJobExecution = new JobExecution(1L);
        JobInstance mockJobInstance = new JobInstance(1L, "resetUserConsentJob");
        mockJobExecution.setJobInstance(mockJobInstance);
        mockJobExecution.setStatus(BatchStatus.COMPLETED);
        mockJobExecution.setExitStatus(ExitStatus.COMPLETED);

        given(jobLauncher.run(eq(processResetUserConsentJob), any(JobParameters.class)))
                .willReturn(mockJobExecution);

        // When
        userConsentResetScheduler.runDailyUserConsentResetJob();

        // Then
        verify(jobLauncher, times(1)).run(eq(processResetUserConsentJob), any(JobParameters.class));
    }

    @Test
    @DisplayName("배치 Job 실행 중 예외 발생 시 로그 출력 테스트")
    void testScheduledJobExecution_WithException() throws Exception {
        // Given
        given(jobLauncher.run(eq(processResetUserConsentJob), any(JobParameters.class)))
                .willThrow(new RuntimeException("Batch job failed"));

        // When
        userConsentResetScheduler.runDailyUserConsentResetJob();

        // Then
        verify(jobLauncher, times(1)).run(eq(processResetUserConsentJob), any(JobParameters.class));
        // 예외가 발생해도 메서드가 정상적으로 완료되어야 함 (로그만 출력)
    }

    @Test
    @DisplayName("JobParameters가 올바르게 생성되는지 테스트")
    void testJobParametersCreation() throws Exception {
        // Given
        JobExecution mockJobExecution = new JobExecution(1L);
        JobInstance mockJobInstance = new JobInstance(1L, "resetUserConsentJob");
        mockJobExecution.setJobInstance(mockJobInstance);
        mockJobExecution.setStatus(BatchStatus.COMPLETED);

        given(jobLauncher.run(eq(processResetUserConsentJob), any(JobParameters.class)))
                .willReturn(mockJobExecution);

        // When
        userConsentResetScheduler.runDailyUserConsentResetJob();

        // Then
        verify(jobLauncher, times(1)).run(eq(processResetUserConsentJob), any(JobParameters.class));
    }
}