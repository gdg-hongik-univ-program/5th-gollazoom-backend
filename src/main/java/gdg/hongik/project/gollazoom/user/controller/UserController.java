package gdg.hongik.project.gollazoom.user.controller;

import gdg.hongik.project.gollazoom.global.api.ApiResponse;
import gdg.hongik.project.gollazoom.user.dto.request.*;

import gdg.hongik.project.gollazoom.user.dto.response.LoginResponse;
import gdg.hongik.project.gollazoom.user.dto.response.MyInfoResponse;
import gdg.hongik.project.gollazoom.user.dto.response.UserWashSettingResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public UserResponse signup(@Valid @RequestBody SignupRequest request) {
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
        Long userId = (Long) auth.getPrincipal();
        userService.changePassword(userId, req);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me")
    @Operation(summary = "내 정보 삭제", description = "등록된 내 정보를 삭제합니다")
    public ResponseEntity<Void> deleteMe() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal();
        userService.deleteMyAccount(userId);
        return ResponseEntity.noContent().build(); // 204
    }

    @PostMapping("/worktime")
    @Operation(summary = "출근시간 입력")
    public ResponseEntity<Void> createWorktime(
            @Valid @RequestBody WorktimeRequest req
    ) {
        Long userId = (Long) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        userService.createWorktime(userId, req.worktime());
        return ResponseEntity.noContent().build(); // 204
    }

    @PatchMapping("/worktime")
    @Operation(summary = "출근시간 수정")
    public ResponseEntity<Void> updateWorktime(
            @Valid @RequestBody WorktimeRequest req
    ) {
        Long userId = (Long) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        userService.updateWorktime(userId, req.worktime());
        return ResponseEntity.noContent().build(); // 204
    }

    @GetMapping("/me")
    @Operation(summary = "내 정보 조회", description = "로그인한 사용자의 내 정보를 조회합니다")
    public ResponseEntity<MyInfoResponse> getMe() {
        Long userId = (Long) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        return ResponseEntity.ok(userService.getMyInfo(userId));
    }

    @GetMapping("/wash-setting")
    public ApiResponse<UserWashSettingResponse> getWashSetting(
            @AuthenticationPrincipal Long userId
    ) {
        return ApiResponse.ok( null, userService.getWashSetting(userId));
    }

    @PatchMapping("/wash-setting")
    public ApiResponse<UserWashSettingResponse> updateWashSetting(
            @AuthenticationPrincipal Long userId,
            @RequestBody UserWashSettingUpdateRequest request
    ) {
        return ApiResponse.ok("세탁 기능 설정이 변경되었어요.",
                userService.updateWashSetting(userId, request));
    }

}

