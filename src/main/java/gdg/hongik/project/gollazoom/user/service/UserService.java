package gdg.hongik.project.gollazoom.user.service;

import gdg.hongik.project.gollazoom.security.JwtTokenProvider;
import gdg.hongik.project.gollazoom.user.dto.request.ChangePasswordRequest;
import gdg.hongik.project.gollazoom.user.dto.request.LoginRequest;
import gdg.hongik.project.gollazoom.user.dto.response.LoginResponse;
import gdg.hongik.project.gollazoom.user.dto.response.MyInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import gdg.hongik.project.gollazoom.user.dto.request.SignupRequest;
import gdg.hongik.project.gollazoom.user.dto.response.UserResponse;
import gdg.hongik.project.gollazoom.user.entity.User;
import gdg.hongik.project.gollazoom.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public UserResponse signup(SignupRequest request) {
        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .nickname(request.nickname())
                .build();
        User saved = userRepository.save(user);

        return new UserResponse(saved.getId(), saved.getUsername(), saved.getNickname());
    }

    public String login(String username, String rawPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new RuntimeException("비밀번호 불일치");
        }
        return jwtTokenProvider.createAccessToken(user.getId());
    }


    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자: " + userId));

        if (!passwordEncoder.matches(req.currentPassword(), user.getPassword())) {
            throw new RuntimeException("현재 비밀번호가 올바르지 않습니다.");
        }

        user.setPassword(passwordEncoder.encode(req.newPassword()));
        // save() 없어도 @Transactional이면 더티체킹으로 반영됨 (그래도 명시해도 됨)
    }

    @Transactional
    public void deleteMyAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저 없음: " + userId));
        userRepository.delete(user);
    }

    @Transactional
    public void createWorktime(Long userId, String worktime) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저 없음"));
        if (user.getWorktime() != null) {
            throw new RuntimeException("이미 worktime이 설정되어 있습니다.");
        }
        user.setWorktime(LocalTime.parse(worktime));
    }

    @Transactional
    public void updateWorktime(Long userId, String worktime) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저 없음"));
        user.setWorktime(LocalTime.parse(worktime));
    }

    public MyInfoResponse getMyInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저 없음"));
        String worktime = null;
        if (user.getWorktime() != null) {
            worktime = user.getWorktime().toString().substring(0, 5);
        }
        return new MyInfoResponse(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                worktime
        );
    }
}

