package gdg.hongik.project.gollazoom.closet.dto;

import gdg.hongik.project.gollazoom.closet.entity.Category;
import gdg.hongik.project.gollazoom.closet.entity.Season;
import gdg.hongik.project.gollazoom.closet.entity.WashStatus;

import java.time.LocalDateTime;

public record ClosetItemListResponse(
        Long clothId,
        String imageUrl,
        Category category,
        Season season,
        boolean isRaining,
        WashStatus washstatus,
        LocalDateTime lastWornAt
) { }
