package gdg.hongik.project.gollazoom.user.repository;

import gdg.hongik.project.gollazoom.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// 나중에 User관련 코드 받았을 때 바꿔버려도 됨
public interface UserRepository extends JpaRepository<User, Long> {
    // 일단 대충 닉네임으로 만들어놓았는데, 아이디 비번으로 바꿀것.
    Optional<User> findByNickname(String nickname);

    boolean existByNickname(String nickname);
}
