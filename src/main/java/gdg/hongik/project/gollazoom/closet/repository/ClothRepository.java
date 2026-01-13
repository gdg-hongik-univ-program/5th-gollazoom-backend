package gdg.hongik.project.gollazoom.closet.repository;

import gdg.hongik.project.gollazoom.closet.entity.Cloth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClothRepository extends JpaRepository<Cloth, Long> {

    // 내 모든 옷을 만든 날짜 내림차순으로 정렬하여 출력한다.
    List<Cloth> findAllByUser_IdOrderByCreatedAtDesc(Long userId);

    // 내 옷 상세목록
    Optional<Cloth> findByIdAndUser_Id(Long clothId, Long userId);

    // wears API에서 여러 옷이 전부 내 옷인지 검증한다.
    List<Cloth> findAllByIdInAndUser_Id(List<Long> clothIds, Long userId);
}
