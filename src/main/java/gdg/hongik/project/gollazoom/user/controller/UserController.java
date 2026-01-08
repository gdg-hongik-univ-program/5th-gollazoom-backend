package gdg.hongik.project.gollazoom.user.controller;

import gdg.hongik.project.gollazoom.user.dto.request.ChangePasswordRequest;
import gdg.hongik.project.gollazoom.user.dto.request.LoginRequest;
import gdg.hongik.project.gollazoom.user.dto.request.SignupRequest;

import gdg.hongik.project.gollazoom.user.dto.response.LoginResponse;
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
    public UserResponse signup(@RequestBody SignupRequest request) {
        UserResponse userResponse = userService.signup(request);
        return userResponse;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        String token = userService.login(request.username(), request.password());
        return new LoginResponse(token);
    }

    @PatchMapping("/password")
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordRequest req) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName(); // JwtAuthenticationFilter에서 setAuthentication 했던 name
        userService.changePassword(username, req);
        return ResponseEntity.noContent().build();
    }
}

