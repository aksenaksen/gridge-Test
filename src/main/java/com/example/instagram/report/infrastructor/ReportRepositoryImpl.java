package com.example.instagram.report.infrastructor;

import com.example.instagram.report.domain.QReport;
import com.example.instagram.report.domain.Report;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.JPQLQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReportRepositoryImpl implements ReportRepositoryCustom{

    private final JPQLQueryFactory queryFactory;

    @Override
    public List<Report> findAll(Long offset, Long limit){
        QReport qReport = QReport.report;

        List<Long> ids = queryFactory.select(qReport.reportId)
                .from(qReport)
                .orderBy(qReport.reportId.desc())
                .limit(limit)
                .offset(offset)
                .fetch();

        return queryFactory.selectFrom(qReport)
                .where(qReport.reportId.in(ids))
                .fetch();
    }

}
