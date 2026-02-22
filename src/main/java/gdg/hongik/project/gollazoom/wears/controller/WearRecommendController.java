package gdg.hongik.project.gollazoom.wears.controller;

import gdg.hongik.project.gollazoom.wears.dto.WearRecommendResponse;
import gdg.hongik.project.gollazoom.wears.service.WearRecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wears")
public class WearRecommendController {
    private final WearRecommendService wearRecommendService;

    @GetMapping("/recommend")
    public WearRecommendResponse recommend(
            @AuthenticationPrincipal Long userId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        return wearRecommendService.recommend(userId, targetDate);
    }
}
