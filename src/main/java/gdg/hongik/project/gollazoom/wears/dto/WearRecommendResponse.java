package gdg.hongik.project.gollazoom.wears.dto;

import java.time.LocalDate;
import java.util.List;

public record WearRecommendResponse(
        LocalDate date,
        boolean isRaining,
        double temperature,
        List<Recommendation> recommendations
) {
    public record Recommendation(
            String type,
            int score,
            List<String> warnings,
            List<ClothSummary> clothes
    ) {}

    public record ClothSummary(
            Long clothId,
            String imageUrl,
            String category,
            String subCategory,
            String colorCode,
            boolean isRaining
    ) {}
}
