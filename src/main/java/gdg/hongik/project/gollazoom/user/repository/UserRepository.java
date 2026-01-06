package gdg.hongik.project.gollazoom.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import gdg.hongik.project.gollazoom.user.entity.User;

public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByUsername(String username);
}
