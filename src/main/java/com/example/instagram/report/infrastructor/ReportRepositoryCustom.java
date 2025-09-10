package com.example.instagram.report.infrastructor;

import com.example.instagram.report.domain.Report;

import java.util.List;

public interface ReportRepositoryCustom {
    List<Report> findAll(Long offset, Long limit);
}
