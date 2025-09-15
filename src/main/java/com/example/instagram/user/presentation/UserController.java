package com.example.instagram.user.presentation;

import com.example.instagram.auth.domain.CustomUserDetails;
import com.example.instagram.user.application.IUserService;
import com.example.instagram.user.application.dto.in.UserDeleteCommand;
import com.example.instagram.user.application.dto.in.UserRegisterCommand;
import com.example.instagram.user.application.dto.in.UserSuspendCommand;
import com.example.instagram.user.presentation.in.RequestCheckUser;
import com.example.instagram.user.presentation.in.RequestFindAllUserCondition;
import com.example.instagram.user.presentation.in.RequestRegisterUser;
import com.example.instagram.user.presentation.in.RequestUpdatePasswordUser;
import com.example.instagram.user.presentation.out.ResponseUser;
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

@Tag(name = "User", description = "사용자 관리 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @Operation(summary = "사용자 회원가입", description = "새로운 사용자를 등록합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 사용자")
    })
    @PostMapping()
    public ResponseEntity<Void> registerUser(
            @Parameter(description = "회원가입 데이터") @Valid @RequestBody RequestRegisterUser req){
        UserRegisterCommand command = req.toCommand();
        userService.register(command);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "사용자명 중복 확인", description = "사용자명 중복 여부를 확인합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "중복 확인 완료"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @GetMapping("/check")
    public ResponseEntity<Void> checkUser(
            @Parameter(description = "중복 확인할 데이터") @Valid @RequestBody RequestCheckUser req){
        if(req.username() != null){
            userService.existsByUsername(req.username());
        }
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "사용자 목록 조회", description = "조건에 따른 사용자 목록을 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자 목록 조회 성공")
    })
    @GetMapping()
    public ResponseEntity<List<ResponseUser>> findAll(
            @Parameter(description = "검색 조건") RequestFindAllUserCondition condition){
        List<ResponseUser> res = userService.findAll(condition).stream()
                .map(ResponseUser::from)
                .toList();
        return ResponseEntity.ok(res);
    }

    @Operation(summary = "사용자 상세 조회", description = "특정 사용자의 상세 정보를 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자 조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<ResponseUser> find(
            @Parameter(description = "사용자 ID") @PathVariable Long userId){
        ResponseUser res = ResponseUser.from(userService.findById(userId));
        return  ResponseEntity.ok(res);
    }

    @Operation(summary = "사용자 정지", description = "관리자가 사용자를 정지시킵니다")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "사용자 정지 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> suspendUser(
            @Parameter(description = "사용자 ID") @PathVariable Long userId){
        userService.suspend(new UserSuspendCommand(userId));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "회원탈퇴", description = "사용자가 자신의 계정을 삭제합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "회원탈퇴 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PatchMapping()
    public ResponseEntity<Void> deleteUser(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails){
        userService.delete(new UserDeleteCommand(userDetails.getUser().getUserId()));
        return ResponseEntity.noContent().build();
    }

//    본인인증 과정이 필요할것 같은데 화면엔 없어서 안넣었습니다 -> 레디스로 구현
    @Operation(summary = "비밀번호 변경", description = "사용자의 비밀번호를 변경합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "비밀번호 변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PutMapping("/password")
    public ResponseEntity<Void> updateUser(
            @Parameter(description = "비밀번호 변경 데이터") @RequestBody @Valid RequestUpdatePasswordUser req){
        userService.changePassword(req.toCommand());
        return ResponseEntity.noContent().build();
    }

}
