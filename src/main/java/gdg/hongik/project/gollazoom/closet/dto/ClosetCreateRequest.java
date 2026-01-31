package gdg.hongik.project.gollazoom.closet.dto;

import gdg.hongik.project.gollazoom.closet.entity.Category;
import gdg.hongik.project.gollazoom.closet.entity.Season;
import gdg.hongik.project.gollazoom.closet.entity.SubCategory;

public record ClosetCreateRequest(
    Category category,
    Season season,
    String color,
    String memo,
    String imageUrl, // 퀵등록 구현을 위해 <option>으로 설정
    SubCategory subCategory, // 퀵등록
    String colorCode, // 퀵등록
    Boolean isRaining
) { }
