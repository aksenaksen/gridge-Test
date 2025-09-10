package com.example.instagram.report.application;

import com.example.instagram.common.Page;
import com.example.instagram.report.application.dto.in.ReportCreateCommand;
import com.example.instagram.report.application.dto.in.ReportStatusUpdateCommand;
import com.example.instagram.report.application.dto.out.ResponseReportDto;
import com.example.instagram.report.application.dto.out.ResponseReportPageDto;
import com.example.instagram.report.domain.ReportStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IReportService {

    void createReport(ReportCreateCommand command);

    ResponseReportDto findReportById(Long reportId);

    @Transactional(readOnly = true)
    ResponseReportPageDto findAllReport(Long page, Long pageSize);

    void updateReportStatus(ReportStatusUpdateCommand command);

    void blockCompleteReport(Long reportId);

    void rejectReport(Long reportId);
}