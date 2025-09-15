package com.example.instagram.comment.presentation;

import com.example.instagram.auth.domain.CustomUserDetails;
import com.example.instagram.comment.application.CommentService;
import com.example.instagram.comment.application.dto.in.CommentBlockCommand;
import com.example.instagram.comment.application.dto.in.CommentDeleteCommand;
import com.example.instagram.comment.application.dto.in.CommentUpdateCommand;
import com.example.instagram.comment.domain.CommentPage;
import com.example.instagram.comment.presentation.in.RequestCreateComment;
import com.example.instagram.comment.presentation.in.RequestUpdateComment;
import com.example.instagram.comment.presentation.out.ResponseComment;
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

import java.util.List;

@Tag(name = "Comment", description = "댓글 관리 API")
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
    
    private final CommentService commentService;
    
    @Operation(summary = "댓글 등록", description = "게시물에 댓글을 등록합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "댓글 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping
    public ResponseEntity<Void> createComment(
            @Parameter(description = "댓글 데이터") @Valid @RequestBody RequestCreateComment request,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        commentService.create(
                request.toCommand(userDetails.getUser().getUserId())
        );
        
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "댓글 상세 조회", description = "특정 댓글의 상세 정보를 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 조회 성공"),
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음")
    })
    @GetMapping("/{commentId}")
    public ResponseEntity<ResponseComment> findComment(
            @Parameter(description = "댓글 ID") @PathVariable Long commentId) {

        ResponseComment res = ResponseComment.from(commentService.findById(commentId));

        return ResponseEntity.ok(res);
    }
    
    @Operation(summary = "댓글 삭제", description = "자신의 댓글을 삭제합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "댓글 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음")
    })
    @PatchMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @Parameter(description = "댓글 ID") @PathVariable Long commentId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        commentService.delete(new CommentDeleteCommand(commentId, userDetails.getUser().getUserId()));

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "댓글 수정", description = "자신의 댓글을 수정합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "댓글 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음")
    })
    @PatchMapping()
    public ResponseEntity<Void> updateComment(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "수정할 댓글 데이터") @RequestBody RequestUpdateComment request
            ){

        commentService.update(new CommentUpdateCommand(request.commentId(), userDetails.getUser().getUserId(), request.content()));

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "댓글 차단", description = "관리자가 댓글을 차단합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "댓글 차단 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{commentId}/block")
    public ResponseEntity<Void> blockComment(
            @Parameter(description = "댓글 ID") @PathVariable Long commentId
    ){

        commentService.block(new CommentBlockCommand(commentId));

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "게시물 댓글 목록 조회", description = "특정 게시물의 댓글 목록을 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "게시물을 찾을 수 없음")
    })
    @GetMapping("/feeds/{feedId}")
    public ResponseEntity<List<ResponseComment>> findCommentsByFeedId(
            @Parameter(description = "게시물 ID") @PathVariable Long feedId,
            @Parameter(description = "마지막 댓글 ID (페이지네이션)") @RequestParam(required = false) String lastCommentId,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "2") Long pageSize) {
        
        List<ResponseComment> res = commentService.findAll(feedId, new CommentPage(pageSize, lastCommentId))
                .stream().map(ResponseComment::from).toList();

        return ResponseEntity.ok(res);
    }

}