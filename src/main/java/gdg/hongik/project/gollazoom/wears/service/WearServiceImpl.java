package gdg.hongik.project.gollazoom.wears.service;


import gdg.hongik.project.gollazoom.closet.entity.Category;
import gdg.hongik.project.gollazoom.closet.entity.Cloth;
import gdg.hongik.project.gollazoom.closet.repository.ClothRepository;
import gdg.hongik.project.gollazoom.user.entity.User;
import gdg.hongik.project.gollazoom.user.repository.UserRepository;
import gdg.hongik.project.gollazoom.wears.dto.*;
import gdg.hongik.project.gollazoom.wears.entity.Wear;
import gdg.hongik.project.gollazoom.wears.entity.WearItem;
import gdg.hongik.project.gollazoom.wears.exception.WearForbiddenException;
import gdg.hongik.project.gollazoom.wears.exception.WearNotFoundException;
import gdg.hongik.project.gollazoom.wears.repository.WearRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
        validateWearCombination(clothes);

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



    @Override
    @Transactional(readOnly = true)
    public Optional<WearGetResponse> getByDate(LocalDate date) {
        // 인증 username 얻기
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        // username으로 User를 조회한다.
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // userId + date로 wear를 조회한다.
        return wearRepository.findByUserIdAndDate(user.getId(), date)
                .map(wear -> new WearGetResponse(
                        wear.getId(),
                        wear.getDate(),
                        wear.getItems().stream()
                                .map(item -> item.getCloth().getId())
                                .toList(),
                        getWearMemoOrNull(wear),
                        wear.getCreatedAt()
                ));

    }

    @Override
    @Transactional
    public WearUpdateResponse update(Long wearId, WearUpdateRequest request) {
        // 인증 username 얻기
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        // username으로 User를 조회한다.
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // wear 조회하고 소유자를 검증한다. 없으면 404
        Wear wear = wearRepository.findByIdAndUserId(wearId, user.getId())
                .orElseThrow(() -> new WearNotFoundException("착용 기록을 찾을 수 없어요."));

        // 입력 검증
        if (request.date() == null) {
            throw new IllegalArgumentException("착용할 날짜를 선택해주세요.");
        }
        if (request.clothIds() == null || request.clothIds().isEmpty()) {
            throw new IllegalArgumentException("최소 1개 이상의 옷을 선택해주세요.");
        }

        // 받은 clothIds가 전부 내 옷인지 검증한다.
        List<Long> clothIds = request.clothIds();
        List<Cloth> clothes = clothRepository.findAllByIdInAndUser_Id(clothIds, user.getId());
        if (clothes.size() != clothIds.size()) {
            // 그 옷이 무슨 옷인지 출력하는 기능 생각중
            throw new IllegalArgumentException("선택한 옷 중 존재하지 않거나 본인의 소유가 아닌 옷이 발견됐어요.");
        }

        // 정보 변경
        wear.changeDate(request.date());

        List<WearItem> newItems = clothes.stream()
                .map(WearItem::of)
                .toList();
        wear.replaceItems(newItems);

        // updatedAt 갱신
        wear.refreshUpdatedAt();

        return new WearUpdateResponse(
                wear.getId(),
                wear.getDate(),
                clothIds,
                wear.getUpdatedAt()
        );
    }

    @Override
    public void delete(Long wearId) {
        // 인증 username 얻기
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        // username으로 User를 조회한다.
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // wear 조회
        Wear wear = wearRepository.findById(wearId)
                .orElseThrow(() -> new WearNotFoundException("착용 기록을 찾을 수 없어요."));

        // 소유자 검증
        if (!wear.getUser().getId().equals(user.getId())) {
            throw new WearForbiddenException("본인의 착용 기록만 삭제할 수 있어요.");
        }

        wearRepository.delete(wear);
    }

    /** Wear 엔티티에 memo 필드 없음, 고로 null 처리.
     *  API를 통해 memo 필드를 받게 된다면 wear.getMemo()로 바꿀 예정.
     */
    private String getWearMemoOrNull(Wear wear) {
        return null;
    }

    // Dress(원피스) 조합 검증 로직
    private void validateWearCombination(List<Cloth> clothes) {
        boolean hasDress = clothes.stream()
                .anyMatch(c -> c.getCategory() == Category.DRESS);
        boolean hasTop = clothes.stream()
                .anyMatch(c -> c.getCategory() == Category.TOP);
        boolean hasBottom = clothes.stream()
                .anyMatch(c -> c.getCategory() == Category.BOTTOM);

        if (hasDress && (hasTop || hasBottom)) {
            throw new IllegalArgumentException(
                    "원피스 종류는 상의/하의와 함께 선택할 수 없어요."
            );
        }
    }

}
