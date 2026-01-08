package gdg.hongik.project.gollazoom.closet.dto;

import gdg.hongik.project.gollazoom.closet.entity.Category;
import gdg.hongik.project.gollazoom.closet.entity.Season;

public record ClosetCreateRequest(
    Category category,
    Season season,
    String color,
    String memo,
    String imageUrl,
    Boolean rainOk
) { }
