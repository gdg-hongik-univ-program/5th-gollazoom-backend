package gdg.hongik.project.gollazoom.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import gdg.hongik.project.gollazoom.user.dto.request.SignupRequest;
import gdg.hongik.project.gollazoom.user.dto.response.UserResponse;
import gdg.hongik.project.gollazoom.user.entity.User;
import gdg.hongik.project.gollazoom.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponse signup(SignupRequest request) {
        User user = User.builder()
                .username(request.username())
                .password(request.password())
                .nickname(request.nickname())
                .build();
        User saved = userRepository.save(user);

        return new UserResponse(saved.getId(), saved.getUsername(), saved.getNickname());
    }

}
