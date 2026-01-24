package gdg.hongik.project.gollazoom.presets.service;

import gdg.hongik.project.gollazoom.presets.dto.response.PresetDetailResponse;
import gdg.hongik.project.gollazoom.presets.dto.response.PresetItemDetailResponse;
import gdg.hongik.project.gollazoom.presets.entity.Preset;
import gdg.hongik.project.gollazoom.presets.entity.PresetItem;
import gdg.hongik.project.gollazoom.presets.repository.PresetRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PresetService {
    private final PresetRepository presetRepository;

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
