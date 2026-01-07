package gdg.hongik.project.gollazoom.user.controller;

import gdg.hongik.project.gollazoom.user.dto.request.LoginRequest;
import gdg.hongik.project.gollazoom.user.dto.request.SignupRequest;

import gdg.hongik.project.gollazoom.user.dto.response.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import gdg.hongik.project.gollazoom.user.dto.response.UserResponse;
import gdg.hongik.project.gollazoom.user.service.UserService;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/users/signup")
    public UserResponse signup(@RequestBody SignupRequest request) {
        UserResponse userResponse = userService.signup(request);
        return userResponse;
    }

    @PostMapping("/users/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        String token = userService.login(request.username(), request.password());
        return new LoginResponse(token);
    }
}

