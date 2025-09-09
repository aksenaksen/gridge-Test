package com.example.instagram.feed_read.presentation;

import com.example.instagram.auth.domain.CustomUserDetails;
import com.example.instagram.common.Page;
import com.example.instagram.feed_read.application.FeedReadService;
import com.example.instagram.feed_read.application.out.ResponseFeedRead;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Feed Read", description = "메인화면 피드 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/feed-read")
public class FeedReadController {

    private final FeedReadService feedReadService;

    @Operation(summary = "메인화면 피드 조회", description = "팔로우하는 사용자들의 게시물을 메인화면에서 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "메인화면 피드 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping()
    public ResponseEntity<List<ResponseFeedRead>> findFollowingFeeds(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") Long limit,
            @Parameter(description = "마지막 게시물 ID (페이지네이션)") @RequestParam(required = false) Long lastFeedId) {
        
        Page page = new Page(limit, lastFeedId);
        List<ResponseFeedRead> feeds = feedReadService.findAll(userDetails.getUser().getUserId(), page);
        
        return ResponseEntity.ok(feeds);
    }
}