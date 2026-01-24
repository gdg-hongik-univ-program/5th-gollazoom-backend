package gdg.hongik.project.gollazoom.presets.service;

import gdg.hongik.project.gollazoom.closet.entity.Cloth;
import gdg.hongik.project.gollazoom.closet.repository.ClothRepository;
import gdg.hongik.project.gollazoom.presets.dto.request.PresetCreateRequest;
import gdg.hongik.project.gollazoom.presets.dto.response.PresetCreateResponse;
import gdg.hongik.project.gollazoom.presets.dto.response.PresetDetailResponse;
import gdg.hongik.project.gollazoom.presets.dto.response.PresetItemDetailResponse;
import gdg.hongik.project.gollazoom.presets.entity.Preset;
import gdg.hongik.project.gollazoom.presets.entity.PresetItem;
import gdg.hongik.project.gollazoom.presets.entity.PresetSlot;
import gdg.hongik.project.gollazoom.presets.repository.PresetRepository;
import gdg.hongik.project.gollazoom.user.entity.User;
import gdg.hongik.project.gollazoom.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PresetService {
    private final PresetRepository presetRepository;
    private final ClothRepository clothRepository;
    private final UserRepository userRepository;

    @Transactional
    public PresetCreateResponse createPreset(Long userId, PresetCreateRequest request) {
        User user=userRepository.findById(userId)
                .orElseThrow();
        Cloth top= clothRepository.findByIdAndUser_Id(request.topClothId(),userId)
                .orElseThrow();
        Cloth bottom=clothRepository.findByIdAndUser_Id(request.bottomClothId(),userId)
                .orElseThrow();
        Preset preset=Preset.of(user,request.name());
        preset.addItem(PresetItem.of(top, PresetSlot.TOP));
        preset.addItem(PresetItem.of(bottom, PresetSlot.BOTTOM));
        if (request.outerClothId() != null) {
            Cloth outer = clothRepository.findByIdAndUser_Id(request.outerClothId(), userId)
                    .orElseThrow();
            preset.addItem(PresetItem.of(outer, PresetSlot.OUTER));
        }
        return new PresetCreateResponse(presetRepository.save(preset).getId());
    }

    public PresetDetailResponse getPresetDetail(Long userId, Long presetId) {
        Preset preset = presetRepository.findByIdAndUserId(presetId, userId)
                .orElseThrow(()->new IllegalArgumentException("cannot find preset"));
        List<PresetItemDetailResponse> items=preset.getItems().stream()
                .sorted(Comparator.comparing(PresetItem::getSlot))
                .map(item->new PresetItemDetailResponse(
                        item.getSlot(),
                        item.getCloth().getId(),
                        item.getCloth().getCategory(),
                        item.getCloth().getSeason(),
                        item.getCloth().getColor(),
                        item.getCloth().getImageUrl(),
                        item.getCloth().isRaining()
                ))
                .toList();
        return new PresetDetailResponse(
                preset.getId(),
                preset.getName(),
                preset.getCreatedAt(),
                items
        );
    }
}
