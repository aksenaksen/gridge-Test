package com.example.instagram.report.application;

import com.example.instagram.common.util.PageLimitCalculator;
import com.example.instagram.feed.application.feed.IFeedService;
import com.example.instagram.report.application.dto.in.ReportCreateCommand;
import com.example.instagram.report.application.dto.in.ReportStatusUpdateCommand;
import com.example.instagram.report.application.dto.out.ResponseReportDto;
import com.example.instagram.report.application.dto.out.ResponseReportPageDto;
import com.example.instagram.report.domain.Report;
import com.example.instagram.user.application.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService implements IReportService {

    private final ReportFinder reportFinder;
    private final ReportCommander reportCommander;
    private final IUserService userService;
    private final IFeedService feedService;

    @Override
    @Transactional
    public void createReport(ReportCreateCommand command) {
        userService.findById(command.reporterId());
        Long writerId = feedService.findFeedById(command.feedId()).writerId();
        reportCommander.create(command, writerId);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseReportDto findReportById(Long reportId) {
        Report report = reportFinder.findById(reportId);
        return ResponseReportDto.from(report);
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseReportPageDto findAllReport(Long page, Long pageSize){
        List<Report> reports = reportFinder.findAll(PageLimitCalculator.calculateOffset(page,pageSize), pageSize);
        Long reportCount = reportFinder.countPage(PageLimitCalculator.calculatePageLimit(page,pageSize,10L));

        List<ResponseReportDto> res = reports.stream().map(ResponseReportDto::from).toList();

        return ResponseReportPageDto.of(res, reportCount);
    }

    @Override
    @Transactional
    public void updateReportStatus(ReportStatusUpdateCommand command) {
        Report report = reportFinder.findById(command.reportId());
        report.updateStatus(command.status());
        reportCommander.save(report);
    }

    @Override
    @Transactional
    public void blockCompleteReport(Long reportId) {
        Report report = reportFinder.findById(reportId);
        report.blockComplete();
    }

    @Override
    @Transactional
    public void rejectReport(Long reportId) {
        Report report = reportFinder.findById(reportId);
        report.reject();
    }
}