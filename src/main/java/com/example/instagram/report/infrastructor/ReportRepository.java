package com.example.instagram.report.infrastructor;

import com.example.instagram.report.domain.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ReportRepository extends JpaRepository<Report, Long>, ReportRepositoryCustom {

    Page<Report> findAllByOrderByReportIdDesc(Pageable pageable);

    // 전체 카운트 x 페이지 크기만큼만 카운트
    @Query(
            value = "select count(*) from (" +
                    "   select report_id from report limit :limit" +
                    ") t",
            nativeQuery = true
    )
    Long countPage(@Param(value = "limit") Long limit);
}