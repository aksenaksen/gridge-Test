package com.example.instagram.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserConsentResetScheduler {

    private final JobLauncher jobLauncher;
    private final Job processResetUserConsentJob;

    @Scheduled(cron = "0 1 0 * * *", zone = "Asia/Seoul")
    public void runDailyUserConsentResetJob(){
        try{
            JobParameters params = new JobParametersBuilder()
                    .addString("cutoffDate", LocalDateTime.now().minusYears(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .toJobParameters();

            JobExecution jobExecution = jobLauncher.run(processResetUserConsentJob, params);
            log.info("✅ 배치 작업 {} 실행완료. 상태: {}", jobExecution.getJobInstance().getJobName(),jobExecution.getStatus());
        } catch (Exception e){
            log.error("❌ Batch Job Failed",e);
        }
    }
}
