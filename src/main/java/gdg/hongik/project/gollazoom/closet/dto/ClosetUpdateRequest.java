package gdg.hongik.project.gollazoom.closet.dto;

import gdg.hongik.project.gollazoom.closet.entity.Category;
import gdg.hongik.project.gollazoom.closet.entity.Season;

public record ClosetUpdateRequest(
        Category category,
        Season season,
        String color,
        String memo,
        String imageUrl,
        boolean isRaining
) { }
