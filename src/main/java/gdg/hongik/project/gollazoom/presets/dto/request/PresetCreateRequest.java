package gdg.hongik.project.gollazoom.presets.dto.request;

public record PresetCreateRequest (
        String name,
        Long topClothId,
        Long bottomClothId,
        Long dressClothId,
        Long outerClothId
){
}
