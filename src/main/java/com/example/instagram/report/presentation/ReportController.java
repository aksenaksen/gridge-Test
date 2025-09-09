package com.example.instagram.report.presentation;

import com.example.instagram.auth.domain.CustomUserDetails;
import com.example.instagram.report.application.IReportService;
import com.example.instagram.report.application.dto.out.ResponseReportDto;
import com.example.instagram.report.application.dto.out.ResponseReportPageDto;
import com.example.instagram.report.presentation.in.RequestCreateReport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Report", description = "신고 관리 API")
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final IReportService reportService;

    @Operation(summary = "신고 등록", description = "게시물에 대한 신고를 등록합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "신고 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping
    public ResponseEntity<Void> createReport(
            @Valid @RequestBody RequestCreateReport request,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {

        reportService.createReport(request.toCommand(userDetails.getUser().getUserId()));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "신고 상세 조회", description = "특정 신고의 상세 정보를 조회합니다 (관리자 전용)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "신고 조회 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "신고를 찾을 수 없음")
    })
    @GetMapping("/{reportId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseReportDto> findReport(
            @Parameter(description = "신고 ID") @PathVariable Long reportId) {
        ResponseReportDto res = reportService.findReportById(reportId);
        return ResponseEntity.ok(res);
    }

    @Operation(summary = "신고 목록 조회", description = "모든 신고 목록을 페이지네이션으로 조회합니다 (관리자 전용)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "신고 목록 조회 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseReportPageDto> findAllReports(
            @Parameter(description = "페이지 크기") @RequestParam Long pageSize, 
            @Parameter(description = "페이지 번호") @RequestParam Long page) {
        ResponseReportPageDto res = reportService.findAllReport(
                page,
                pageSize
        );
        return ResponseEntity.ok(res);
    }

    @Operation(summary = "신고 처리 - 차단", description = "신고된 콘텐츠를 차단 처리합니다 (관리자 전용)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "차단 처리 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "신고를 찾을 수 없음")
    })
    @PatchMapping("/{reportId}/block")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> blockCompleteReport(
            @Parameter(description = "신고 ID") @PathVariable Long reportId) {
        reportService.blockCompleteReport(reportId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "신고 처리 - 거부", description = "신고를 거부 처리합니다 (관리자 전용)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "거부 처리 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "신고를 찾을 수 없음")
    })
    @PatchMapping("/{reportId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> rejectReport(
            @Parameter(description = "신고 ID") @PathVariable Long reportId) {
        reportService.rejectReport(reportId);
        return ResponseEntity.ok().build();
    }


}