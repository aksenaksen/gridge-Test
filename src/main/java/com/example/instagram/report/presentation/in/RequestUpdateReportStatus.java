package com.example.instagram.report.presentation.in;

import com.example.instagram.report.application.dto.in.ReportStatusUpdateCommand;
import com.example.instagram.report.domain.ReportStatus;
import jakarta.validation.constraints.NotNull;

public record RequestUpdateReportStatus(
        @NotNull
        ReportStatus status
) {
    public ReportStatusUpdateCommand toCommand(Long reportId) {
        return new ReportStatusUpdateCommand(reportId, status);
    }
}