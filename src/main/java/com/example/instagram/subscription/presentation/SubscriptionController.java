package com.example.instagram.subscription.presentation;

import com.example.instagram.auth.domain.CustomUserDetails;
import com.example.instagram.subscription.application.SubscriptionService;
import com.example.instagram.subscription.application.dto.in.SubscriptionBlockCommand;
import com.example.instagram.subscription.application.dto.in.SubscriptionFollowCommand;
import com.example.instagram.subscription.application.dto.in.SubscriptionUnFollowCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Subscription", description = "팔로우 관리 API")
@RestController
@RequestMapping("/api/subscription")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Operation(summary = "팔로우", description = "다른 사용자를 팔로우합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "팔로우 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PostMapping("/{userId}")
    public ResponseEntity<Void> follow(
            @Parameter(description = "팔로우할 사용자 ID") @PathVariable("userId") Long followeeId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails)
    {
        subscriptionService.follow(new SubscriptionFollowCommand(followeeId, userDetails.getUser().getUserId()));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "언팔로우", description = "팔로우를 취소합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "언팔로우 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PatchMapping("/{userId}")
    public ResponseEntity<Void> unfollow(
            @Parameter(description = "언팔로우할 사용자 ID") @PathVariable("userId") Long followeeId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails)
    {
        subscriptionService.unfollow(new SubscriptionUnFollowCommand(followeeId, userDetails.getUser().getUserId()));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "사용자 차단", description = "다른 사용자를 차단합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "사용자 차단 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PatchMapping("/block/{userId}")
    public ResponseEntity<Void> block(
            @Parameter(description = "차단할 사용자 ID") @PathVariable("userId") Long followeeId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails)
    {
        subscriptionService.block(new SubscriptionBlockCommand(followeeId, userDetails.getUser().getUserId()));
        return ResponseEntity.noContent().build();
    }
}
