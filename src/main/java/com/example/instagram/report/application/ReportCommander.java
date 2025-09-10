package com.example.instagram.report.application;

import com.example.instagram.common.util.Snowflake;
import com.example.instagram.report.application.dto.in.ReportCreateCommand;
import com.example.instagram.report.domain.Report;
import com.example.instagram.report.infrastructor.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReportCommander {

    private final ReportRepository reportRepository;
    private final Snowflake snowflake = new Snowflake();

    public void create(ReportCreateCommand command, Long feedWriterId) {
        Report.checkAvailable(command.reporterId(), feedWriterId);
        Report report = Report.createReport(
                snowflake.nextId(),
                command.feedId(),
                command.reporterId(),
                command.reason()
        );
        reportRepository.save(report);
    }

    public void save(Report report) {
        reportRepository.save(report);
    }

    public void delete(Report report) {
        reportRepository.delete(report);
    }
}