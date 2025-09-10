package com.example.instagram.report.application.dto.out;

import com.example.instagram.report.domain.Report;
import com.example.instagram.report.domain.ReportStatus;

import java.time.LocalDateTime;

public record ResponseReportDto(
        Long reportId,
        Long reporterId,
        Long feedId,
        Long commentId,
        String reason,
        ReportStatus status,
        LocalDateTime createdAt
) {
    public static ResponseReportDto from(Report report) {
        return new ResponseReportDto(
                report.getReportId(),
                report.getReporterId(),
                report.getFeedId(),
                report.getCommentId(),
                report.getReason(),
                report.getStatus(),
                report.getCreatedAt()
        );
    }
}