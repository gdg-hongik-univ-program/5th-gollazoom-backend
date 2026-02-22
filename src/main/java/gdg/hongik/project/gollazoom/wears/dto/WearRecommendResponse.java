package gdg.hongik.project.gollazoom.wears.dto;

import java.time.LocalDate;
import java.util.List;

public record WearRecommendResponse (
        LocalDate date,
        boolean isRaining,
        double temperature,
        List<Recommendation> recommendations
){
    public record Recommendation(
            String type, // 프리셋인지, Dress인지, Top_Bottom 조합인지
            int score, // 점수(디버깅)
            List<String> warnings, // 감점 사유 (디버깅)
            List<Long> clothIds
    ) {}
}
