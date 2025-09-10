package com.example.instagram.report.presentation.in;

import com.example.instagram.report.application.dto.in.ReportCreateCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import static com.example.instagram.report.constant.ReportMessageConstant.EMPTY_REASON;
import static com.example.instagram.report.constant.ReportMessageConstant.INVALID_REASON_LENGTH;

public record RequestCreateReport(
        Long feedId,
        Long commentId,
        @NotBlank(message = EMPTY_REASON)
        @Size(min = 10, max = 500, message = INVALID_REASON_LENGTH)
        String reason
) {
    public ReportCreateCommand toCommand(Long reporterId) {
        return new ReportCreateCommand(reporterId, feedId, commentId, reason);
    }
}