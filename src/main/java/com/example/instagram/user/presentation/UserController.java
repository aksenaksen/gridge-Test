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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @PostMapping
    public ResponseEntity<Void> registerUser(@Valid @RequestBody RequestRegisterUser req){
        UserRegisterCommand command = req.toCommand();
        userService.register(command);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check")
    public ResponseEntity<Void> checkUser(@Valid @RequestBody RequestCheckUser req){
        if(req.username() != null){
            userService.existsByUsername(req.username());
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping()
    public ResponseEntity<List<ResponseUser>> findAll(RequestFindAllUserCondition condition){
        List<ResponseUser> res = userService.findAll(condition).stream()
                .map(ResponseUser::from)
                .toList();
        return ResponseEntity.ok(res);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ResponseUser> find(@PathVariable Long userId){
        ResponseUser res = ResponseUser.from(userService.findById(userId));
        return  ResponseEntity.ok(res);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> suspendUser(@PathVariable Long userId){
        userService.suspend(new UserSuspendCommand(userId));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping()
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal CustomUserDetails userDetails){
        userService.delete(new UserDeleteCommand(userDetails.getUser().getUserId()));
        return ResponseEntity.noContent().build();
    }

//    본인인증 과정이 필요할것 같은데 화면엔 없어서 안넣었습니다 -> 레디스로 구현가능할듯
    @PutMapping("/password")
    public ResponseEntity<Void> updateUser(RequestUpdatePasswordUser req){
        userService.changePassword(req.toCommand());
        return ResponseEntity.noContent().build();
    }

}
