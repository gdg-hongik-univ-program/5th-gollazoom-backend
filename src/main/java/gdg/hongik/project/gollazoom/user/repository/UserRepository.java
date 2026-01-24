package gdg.hongik.project.gollazoom.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import gdg.hongik.project.gollazoom.user.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);
    Optional<User> findById(Long userId);
}

