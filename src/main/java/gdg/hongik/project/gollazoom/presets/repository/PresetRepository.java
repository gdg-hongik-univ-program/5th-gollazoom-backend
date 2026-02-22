package gdg.hongik.project.gollazoom.presets.repository;

import gdg.hongik.project.gollazoom.presets.entity.Preset;
import gdg.hongik.project.gollazoom.user.entity.User;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PresetRepository extends JpaRepository<Preset, Long> {
    List<Preset> findAllByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<Preset> findByIdAndUserId(Long id, Long userId);

    Long user(User user);
}
