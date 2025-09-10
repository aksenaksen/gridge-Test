package com.example.instagram.report.application;

import com.example.instagram.report.domain.Report;
import com.example.instagram.report.exception.ReportNotFoundException;
import com.example.instagram.report.infrastructor.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ReportFinder {

    private final ReportRepository reportRepository;


    public Report findById(Long reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(ReportNotFoundException::new);
    }

    public List<Report> findAll(Long offset, Long limit) {
        return reportRepository.findAll(offset, limit);
    }

    public Long countPage(Long limit){
        return reportRepository.countPage(limit);
    }
}