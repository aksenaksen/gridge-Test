package com.example.instagram.feed.presentation;

import com.example.instagram.auth.domain.CustomUserDetails;
import com.example.instagram.feed.application.like.FeedLikeService;
import com.example.instagram.feed.application.dto.in.LikeCommand;
import com.example.instagram.feed.application.dto.in.UnLikeCommand;
import com.example.instagram.feed.application.dto.out.ResponseFeedLikeCount;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Feed Like", description = "게시물 좋아요 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/feeds")
public class FeedLikeController {

    private final FeedLikeService feedLikeService;

    @Operation(summary = "좋아요 등록", description = "게시물에 좋아요를 등록합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "좋아요 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping("/{feedId}/like")
    public ResponseEntity<String> likeFeed(
            @Parameter(description = "게시물 ID") @PathVariable Long feedId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user) {
        
        LikeCommand command = new LikeCommand(feedId, user.getUser().getUserId());
        feedLikeService.like(command);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "좋아요 취소", description = "게시물의 좋아요를 취소합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "좋아요 취소 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @DeleteMapping("/{feedId}/like")
    public ResponseEntity<String> unlikeFeed(
            @Parameter(description = "게시물 ID") @PathVariable Long feedId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user) {
        
        UnLikeCommand command = new UnLikeCommand(feedId, user.getUser().getUserId());
        feedLikeService.unlike(command);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "좋아요 수 조회", description = "게시물의 좋아요 수를 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "좋아요 수 조회 성공"),
            @ApiResponse(responseCode = "404", description = "게시물을 찾을 수 없음")
    })
    @GetMapping("/{feedId}/like-count")
    public ResponseEntity<ResponseFeedLikeCount> findLikeCount(
            @Parameter(description = "게시물 ID") @PathVariable Long feedId) {
        ResponseFeedLikeCount response = feedLikeService.findLikeCount(feedId);
        return ResponseEntity.ok(response);
    }
}