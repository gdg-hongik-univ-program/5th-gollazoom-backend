package gdg.hongik.project.gollazoom.closet.dto;

import gdg.hongik.project.gollazoom.closet.entity.Category;
import gdg.hongik.project.gollazoom.closet.entity.Season;

public record ClosetItemListResponse(
        Long clothId,
        String imageUrl,
        Category category,
        Season season,
        boolean rainOk
        // LocalDateTime lastWornAt 은 wears가 구현되고 나서 추가하겠습니다.
) { }
