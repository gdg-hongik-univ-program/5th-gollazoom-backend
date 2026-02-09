package gdg.hongik.project.gollazoom.closet.dto;

import gdg.hongik.project.gollazoom.closet.entity.Category;
import gdg.hongik.project.gollazoom.closet.entity.Season;

import java.time.LocalDateTime;

public record ClosetResponse(
        long clothId,
        Category category,
        Season season,
        String color,
        String memo,
        String imageUrl,
        boolean isRaining,
        LocalDateTime createAt,
        LocalDateTime lastWornAt
) {}
