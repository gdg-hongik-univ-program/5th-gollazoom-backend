package gdg.hongik.project.gollazoom.wears.service;


import gdg.hongik.project.gollazoom.closet.entity.Cloth;
import gdg.hongik.project.gollazoom.closet.repository.ClothRepository;
import gdg.hongik.project.gollazoom.user.entity.User;
import gdg.hongik.project.gollazoom.user.repository.UserRepository;
import gdg.hongik.project.gollazoom.wears.dto.WearCreateRequest;
import gdg.hongik.project.gollazoom.wears.dto.WearCreateResponse;
import gdg.hongik.project.gollazoom.wears.entity.Wear;
import gdg.hongik.project.gollazoom.wears.entity.WearItem;
import gdg.hongik.project.gollazoom.wears.repository.WearRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WearServiceImpl implements WearService{

    private final WearRepository wearRepository;
    private final UserRepository userRepository;
    private final ClothRepository clothRepository;

    @Override
    @Transactional
    public WearCreateResponse create(WearCreateRequest request) {
        // 인증 username 얻기
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        // username으로 User를 조회한다.
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 날짜 중복 방지 예외처리
        if (wearRepository.existsByUserIdAndDate(user.getId(), request.date())) {
            throw new IllegalArgumentException("이미 해당 날짜에 착용 기록이 존재해요.");
        }

        // clothIds 제대로 되어있는지 검증, 내 옷인지 아닌지 판단하는 예외처리
        List<Long> clothIds = request.clothIds();
        if (clothIds == null || clothIds.isEmpty()) {
            throw new IllegalArgumentException("최소 1개 이상의 옷을 등록해야 해요.");
        }

        List<Cloth> clothes = clothRepository.findAllByIdInAndUser_Id(clothIds, user.getId());
        if (clothes.size() != clothIds.size()) {
            throw new IllegalArgumentException("추가한 옷 중 존재하지 않는 옷이 있어요.");
        }

        // 모든 조건을 만족했으면 Wear 생성 + WearItem 연결
        Wear wear = Wear.of(user, request.date());
        for (Cloth cloth : clothes) {
            wear.addItem(WearItem.of(cloth));
        }

        Wear saved = wearRepository.save(wear);

        // 응답 (clothes 반환 순서는 따로 정하지 않았음. 필요 시 Map이나 ORDER BY 조치 취할 것.
        return new WearCreateResponse(
                saved.getId(),
                saved.getDate(),
                clothIds,
                saved.getCreatedAt()
        );
    }
}
