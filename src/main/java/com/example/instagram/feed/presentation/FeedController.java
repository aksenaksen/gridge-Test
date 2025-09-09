package com.example.instagram.feed.presentation;

import com.example.instagram.auth.domain.CustomUserDetails;
import com.example.instagram.feed.application.feed.IFileUploadService;
import com.example.instagram.feed.application.feed.IFeedService;
import com.example.instagram.feed.application.dto.in.FeedDeleteByAdminCommand;
import com.example.instagram.feed.application.dto.in.FeedDeleteCommand;
import com.example.instagram.feed.application.dto.out.ResponseFeedDto;
import com.example.instagram.feed.presentation.in.RequestCreateFeed;
import com.example.instagram.feed.presentation.in.RequestFindFeedCondition;
import com.example.instagram.feed.presentation.in.RequestUpdateFeed;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Feed", description = "인스타그램 게시물 API")
@RestController
@RequestMapping("/api/feeds")
@RequiredArgsConstructor
public class FeedController {
    
    private final IFeedService feedService;
    private final IFileUploadService fileUploadService;
    
    @Operation(summary = "게시물 등록", description = "새로운 게시물을 등록합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시물 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping()
    public ResponseEntity<Void> createFeed(
            @Parameter(description = "게시물 데이터") @RequestPart("data") @Valid RequestCreateFeed request,
            @Parameter(description = "이미지 파일들") @RequestPart("image") List<MultipartFile> imageFiles,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        List<String> imageUrls = fileUploadService.uploadMultipleFiles(imageFiles, "feeds");
        
        feedService.createFeed(request.toCommand(userDetails.getUser().getUserId(), imageUrls));

        return ResponseEntity.ok().build();
    }
    
    @Operation(summary = "게시물 상세 조회", description = "특정 게시물의 상세 정보를 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시물 조회 성공"),
            @ApiResponse(responseCode = "404", description = "게시물을 찾을 수 없음")
    })
    @GetMapping("/{feedId}")
    public ResponseEntity<ResponseFeedDto> findFeed(
            @Parameter(description = "게시물 ID") @PathVariable Long feedId) {
        ResponseFeedDto response = feedService.findFeedById(feedId);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "게시물 목록 조회", description = "조건에 따른 게시물 목록을 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시물 목록 조회 성공")
    })
    @GetMapping
    public ResponseEntity<List<ResponseFeedDto>> findAllFeedsWithCondition(
            @Parameter(description = "검색 조건") RequestFindFeedCondition req) {
        
        List<ResponseFeedDto> response = feedService.findAllFeedWithCondition(req.toPage(), req);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "게시물 삭제", description = "자신의 게시물을 삭제합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "게시물 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "게시물을 찾을 수 없음")
    })
    @PatchMapping("/{feedId}")
    public ResponseEntity<Void> deleteFeed(
            @Parameter(description = "게시물 ID") @PathVariable Long feedId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {

        feedService.deleteFeed(new FeedDeleteCommand(feedId, userDetails.getUser().getUserId()));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "게시물 강제 삭제", description = "관리자가 게시물을 강제 삭제합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "게시물 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "게시물을 찾을 수 없음")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{feedId}/admin")
    public ResponseEntity<Void> deleteFeedByAdmin(
            @Parameter(description = "게시물 ID") @PathVariable Long feedId
            ) {

        feedService.deleteFeedByAdmin(new FeedDeleteByAdminCommand(feedId));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "게시물 수정", description = "자신의 게시물을 수정합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시물 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "게시물을 찾을 수 없음")
    })
    @PutMapping(value = "/{feedId}")
    public ResponseEntity<Void> updateFeed(
            @Parameter(description = "게시물 ID") @PathVariable Long feedId,
            @Parameter(description = "수정할 게시물 데이터") @Valid @RequestBody RequestUpdateFeed request,
            @Parameter(description = "새로운 이미지 파일들 (선택사항)") @RequestPart(value = "images", required = false) List<MultipartFile> imageFiles,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {

        List<String> imageUrls = (imageFiles != null && !imageFiles.isEmpty())
                ? fileUploadService.uploadMultipleFiles(imageFiles, "feeds")
                : List.of();

        feedService.updateFeed(request.toCommand(feedId, imageUrls, userDetails.getUser().getUserId()));
        return ResponseEntity.ok().build();
    }
    

}