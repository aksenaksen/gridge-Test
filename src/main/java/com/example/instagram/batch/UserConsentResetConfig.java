package com.example.instagram.batch;

import com.example.instagram.user.domain.AgreementType;
import com.example.instagram.user.domain.User;
import com.example.instagram.user.domain.UserStatus;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class UserConsentResetConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory emf;

    @Bean
    public Job processResetUserConsentJob(Step resetUserConsentStep) {
        return new JobBuilder("resetUserConsentJob", jobRepository)
                .start(resetUserConsentStep)
                .build();
    }

    @Bean
    public Step resetUserConsentStep(
            JpaCursorItemReader<User> resetUserConsentItemReader
            ,ItemProcessor<User, User> resetUserConsentItemProcessor
            ,JpaItemWriter<User> resetUserConsentItemWriter) {
        return new StepBuilder("resetUserConsentStep", jobRepository)
                .<User, User>chunk(100, transactionManager)
                .reader(resetUserConsentItemReader)
                .processor(resetUserConsentItemProcessor)
                .writer(resetUserConsentItemWriter)
                .build();
    }

    @Bean
    @StepScope
    public JpaCursorItemReader<User> resetUserConsentItemReader(
            @Value("#{jobParameters['cutoffDate']}") String cutoffDateString
    ) {
        LocalDateTime cutoffDate = LocalDateTime.parse(cutoffDateString);
        return new JpaCursorItemReaderBuilder<User>()
                .name("resetUserConsentItemReader")
                .entityManagerFactory(emf)
                .queryString("select distinct u from User u " +
                        "join u.agreements ua " +
                        "where u.status = :activeStatus " +
                        "and ua.agreedAt < :cutoffDate " +
                        "and ua.isAgreed = true " +
                        "and ua.agreementType = :agreementType")
                .parameterValues(Map.of(
                        "activeStatus", UserStatus.ACTIVE,
                        "cutoffDate", cutoffDate,
                        "agreementType", AgreementType.TERMS_OF_SERVICE
                ))
                .build();
    }

    @Bean
    public ItemProcessor<User, User> resetUserConsentItemProcessor() {
        return user -> {
            user.withdrawAgreement(AgreementType.TERMS_OF_SERVICE);
            return user;
        };
    }

    @Bean
    public JpaItemWriter<User> resetUserConsentItemWriter() {
        return new JpaItemWriterBuilder<User>()
                .entityManagerFactory(emf)
                .usePersist(false)
                .build();
    }
}
