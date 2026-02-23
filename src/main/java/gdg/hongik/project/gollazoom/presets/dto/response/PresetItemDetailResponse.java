package gdg.hongik.project.gollazoom.presets.dto.response;

import gdg.hongik.project.gollazoom.closet.entity.Category;
import gdg.hongik.project.gollazoom.closet.entity.Season;
import gdg.hongik.project.gollazoom.closet.entity.SubCategory;
import gdg.hongik.project.gollazoom.presets.entity.PresetSlot;

public record PresetItemDetailResponse(
        PresetSlot slot,
        Long clothId,
        Category category,
        Season season,
        String color,
        String imageUrl,
        boolean isRaining,
        SubCategory subCategory,
        String colorCode
) {
}
