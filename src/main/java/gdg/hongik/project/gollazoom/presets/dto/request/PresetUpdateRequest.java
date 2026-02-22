package gdg.hongik.project.gollazoom.presets.dto.request;

public record PresetUpdateRequest(
        String name,
        Long topClothId,
        Long bottomClothId,
        Long dressClothId,
        Long outerClothId
) {
}
