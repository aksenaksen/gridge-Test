package com.example.instagram.report.presentation.in;

import com.example.instagram.common.Page;
import com.example.instagram.report.domain.ReportStatus;

public record RequestFindAllReportCondition(
        Long limit,
        Long lastReportId,
        ReportStatus status,
        Long reporterId
) {
    public Page toPage() {
        return new Page(limit != null ? limit : 20L, lastReportId);
    }
}