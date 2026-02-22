package gdg.hongik.project.gollazoom.presets.dto.response;

import java.time.LocalDateTime;

public record PresetListResponse(
        Long presetId,
        String name,
        LocalDateTime createdAt
) {
}
