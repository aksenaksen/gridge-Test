package com.example.instagram.report.application.dto.out;

import java.util.List;

public record ResponseReportPageDto (
    List<ResponseReportDto> reports,
    Long reportCount)
{

    public static ResponseReportPageDto of(List<ResponseReportDto> reportDto, Long reportCount) {
        return new ResponseReportPageDto(reportDto ,reportCount);
    }
}
