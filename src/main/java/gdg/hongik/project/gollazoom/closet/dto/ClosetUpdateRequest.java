package gdg.hongik.project.gollazoom.closet.dto;

import gdg.hongik.project.gollazoom.closet.entity.Category;
import gdg.hongik.project.gollazoom.closet.entity.Season;
import gdg.hongik.project.gollazoom.closet.entity.SubCategory;

public record ClosetUpdateRequest(
        Category category,
        Season season,
        String color,
        String memo,
        String imageUrl,
        SubCategory subCategory,
        String colorCode,
        boolean isRaining
) { }
