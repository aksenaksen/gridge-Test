package com.example.instagram.report.application.dto.in;

import com.example.instagram.report.domain.ReportStatus;

public record ReportStatusUpdateCommand(
        Long reportId,
        ReportStatus status
) {
}