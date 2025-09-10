package com.example.instagram.report.application.dto.in;

public record ReportCreateCommand(
        Long reporterId,
        Long feedId,
        Long commentId,
        String reason
) {
}