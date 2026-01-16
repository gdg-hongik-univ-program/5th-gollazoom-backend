package gdg.hongik.project.gollazoom.user.controller;

import gdg.hongik.project.gollazoom.user.dto.request.ChangePasswordRequest;
import gdg.hongik.project.gollazoom.user.dto.request.LoginRequest;
import gdg.hongik.project.gollazoom.user.dto.request.SignupRequest;

import gdg.hongik.project.gollazoom.user.dto.response.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import gdg.hongik.project.gollazoom.user.dto.response.UserResponse;
import gdg.hongik.project.gollazoom.user.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "회원가입을 진행합니다")
    public UserResponse signup(@Valid  @RequestBody SignupRequest request) {
        UserResponse userResponse = userService.signup(request);
        return userResponse;
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "로그인을 진행합니다")
    public LoginResponse login(@RequestBody LoginRequest request) {
        String token = userService.login(request.username(), request.password());
        return new LoginResponse(token);
    }

    @PatchMapping("/password")
    @Operation(summary = "비밀번호 변경", description = "비밀번호를 변경합니다")
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordRequest req) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal(); // ✅ 이제 userId
        userService.changePassword(userId, req);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me")
    @Operation(summary = "내 정보 삭제", description = "등록된 내 정보를 삭제합니다")
    public ResponseEntity<Void> deleteMe() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal(); // ✅ 이제 userId
        userService.deleteMyAccount(userId);
        return ResponseEntity.noContent().build(); // 204
    }
}

