package gdg.hongik.project.gollazoom.closet.service;

import gdg.hongik.project.gollazoom.closet.dto.ClosetCreateRequest;
import gdg.hongik.project.gollazoom.closet.dto.ClosetItemListResponse;
import gdg.hongik.project.gollazoom.closet.dto.ClosetResponse;
import gdg.hongik.project.gollazoom.closet.dto.ClosetUpdateRequest;
import gdg.hongik.project.gollazoom.closet.entity.Cloth;
import gdg.hongik.project.gollazoom.closet.repository.ClothRepository;
import gdg.hongik.project.gollazoom.user.User;
import gdg.hongik.project.gollazoom.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ClosetServiceImpl implements ClosetService {
    private final ClothRepository clothRepository;
    private final UserRepository userRepository;

    /**
     * ClosetCreateRequest DTO에서 받아온 변수들을 통해 새 옷 객체를 생성합니다.
     * @param userId : 생셩 유저 Id, 이는 닉네임이나 아이디로 변경될 수 있습니다.
     * @param request : 변수들의 집합입니다.
     * @return : 생성된 옷의 정보들 즉, 변수들의 값을 toResponse를 이용해 반환합니다.
     */
    @Override
    public ClosetResponse create(Long userId, ClosetCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Cloth cloth = new Cloth(
                user,
                request.category(),
                request.season(),
                request.color(),
                request.memo(),
                request.imageUrl(),
                request.rainOk()
        );

        Cloth saved = clothRepository.save(cloth);
        return toResponse(saved);
    }

    /**
     * 모든 옷의 리스트를 불러옵니다.
     * @param userId : 생성한 유저의 Id입니다. 설명은 위와 같습니다.
     * @return : 옷의 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public List<ClosetItemListResponse> list(Long userId) {
        return clothRepository.findAllByUser_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toListItem)
                .toList();
    }

    /**
     * clothId에 해당하는 옷의 상세 정보들을 확인합니다.
     * @param userId : 위와 같습니다.
     * @param clothId : 조회하려는 옷의 고유 Id입니다.
     * @return : 옷의 상세한 변수들을 toResponse를 이용해 모두 출력합니다.
     */
    @Override
    @Transactional(readOnly = true)
    public ClosetResponse get(Long userId, Long clothId) {
        Cloth cloth = clothRepository.findByIdAndUser_Id(clothId, userId)
                .orElseThrow(() -> new IllegalArgumentException("옷을 찾을 수 없습니다."));

        return toResponse(cloth);
    }

    /**
     * 옷의 상세 정보들을 새 값으로 업데이트합니다.
     * @param userId : 위와 같습니다.
     * @param clothId : 업데이트할 옷의 고우 Id입니다.
     * @param request : 바뀔 변수값의 정보가 들어있습니다.
     * @return : 값이 어떻게 바뀌었는지에 대해 toResponse를 이용해 반환합니다.
     */
    @Override
    public ClosetResponse update(Long userId, Long clothId, ClosetUpdateRequest request) {
        Cloth cloth = clothRepository.findByIdAndUser_Id(clothId, userId)
                .orElseThrow(() -> new IllegalArgumentException("옷을 찻을 수 없습니다."));

        cloth.update(
                request.category(),
                request.season(),
                request.color(),
                request.memo(),
                request.imageUrl(),
                request.rainOk()
        );

        return toResponse(cloth);
    }

    /**
     * 지정된 옷을 삭제합니다.
     * @param userId : 위와 같습니다.
     * @param clothId : 삭제할 옷의 Id입니다.
     */
    @Override
    public void delete(Long userId, Long clothId) {
        Cloth cloth = clothRepository.findByIdAndUser_Id(clothId, userId)
                .orElseThrow(() -> new IllegalArgumentException("옷을 찾을 수 없습니다."));

        clothRepository.delete(cloth);
    }

    /**
     * 옷의 모든 변수 값을 반환하는 메서드입니다.
     * @param cloth : 대상 옷 객체입니다.
     * @return : 옷의 id, 카테고리, 계절, 색깔, 메모 등을 모두 반환합니다.
     */
    private ClosetResponse toResponse(Cloth cloth) {
        return new ClosetResponse(
                cloth.getId(),
                cloth.getCategory(),
                cloth.getSeason(),
                cloth.getColor(),
                cloth.getMemo(),
                cloth.getImageUrl(),
                cloth.isRainOk(),
                cloth.getCreatedAt()
        );
    }

    /**
     * 모든 옷의 리스트를 반환할 때 리턴할 변수 값입니다. 상세 조회보다는 적은 정보를 반환합니다.
     * @param cloth : 가지고 있는 옷의 객체입니다.
     * @return : 옷의 id, 이미지, 카테고리, 계절, 우천 시 착용 가능 여부를 반환합니다.
     */
    private ClosetItemListResponse toListItem(Cloth cloth) {
        return new ClosetItemListResponse(
                cloth.getId(),
                cloth.getImageUrl(),
                cloth.getCategory(),
                cloth.getSeason(),
                cloth.isRainOk()
        );
    }
}
