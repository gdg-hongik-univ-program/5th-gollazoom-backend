package gdg.hongik.project.gollazoom.presets.dto.response;

import java.util.List;

public record PresetWashCheckResponse(
        boolean hasWashing, // 세탁중 옷이 1개라도 있으면 true
        List<Long> washingClothIds
) {}
