package com.example.instagram.report.domain;

import com.example.instagram.common.BaseEntity;
import com.example.instagram.common.RootBaseEntity;
import com.example.instagram.common.events.ReportBlockEvent;
import com.example.instagram.report.application.dto.in.ReportCreateCommand;
import com.example.instagram.report.constant.ReportMessageConstant;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reports")
public class Report extends RootBaseEntity<Report> {

    @Id
    @Column(name = "report_id")
    private Long reportId;

    @Column(name = "reporter_id", nullable = false)
    private Long reporterId;

    @Column(name = "feed_id")
    private Long feedId;

    @Column(name = "comment_id")
    private Long commentId;

    @Column(name = "reason", nullable = false, length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReportStatus status;

    @Builder
    public Report(Long reporterId, Long feedId, Long commentId, String reason, ReportStatus status) {
        validateReportTarget(feedId, commentId);
        this.reporterId = reporterId;
        this.feedId = feedId;
        this.commentId = commentId;
        this.reason = reason;
        this.status = status != null ? status : ReportStatus.PENDING;
    }

    public static Report createReport(Long reportId, Long feedId, Long reporterId, String reason) {
        Report report = new Report();
        report.reporterId = reporterId;
        report.feedId = feedId;
        report.reportId = reportId;
        report.status = ReportStatus.PENDING;
        report.reason = reason;
        return report;
    }
    public static void checkAvailable(Long reporterId, Long feedUserId){
        if(Objects.equals(reporterId, feedUserId)){
            throw new RuntimeException();
        }
    }

    private void validateReportTarget(Long feedId, Long commentId) {
        if (feedId == null && commentId == null) {
            throw new IllegalArgumentException(ReportMessageConstant.INVALID_REPORT_TARGET_NULL);
        }
        if (feedId != null && commentId != null) {
            throw new IllegalArgumentException(ReportMessageConstant.CANNOT_REPORT_BOTH_TARGET);
        }
    }

    public void blockComplete() {
        this.status = ReportStatus.BLOCK_COMPLETED;
        this.registerEvent(new ReportBlockEvent(this.feedId,this.reporterId));
    }

    public void reject() {
        this.status = ReportStatus.REJECTED;
    }

    public void updateStatus(ReportStatus status) {
        this.status = status;
    }
}