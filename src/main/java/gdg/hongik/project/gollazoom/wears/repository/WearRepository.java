package gdg.hongik.project.gollazoom.wears.repository;

import gdg.hongik.project.gollazoom.wears.entity.Wear;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface WearRepository extends JpaRepository<Wear, Long> {
    Optional<Wear> findByUserIdAndDate(Long userid, LocalDate date);
    boolean existsByUserIdAndDate(Long userId, LocalDate date);
}
