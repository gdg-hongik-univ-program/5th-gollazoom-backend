package gdg.hongik.project.gollazoom.presets.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record PresetDetailResponse(
        Long presetId,
        String name,
        LocalDateTime createdAt,
        List<PresetItemDetailResponse> items
) {
}
