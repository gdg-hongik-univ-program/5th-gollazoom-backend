package gdg.hongik.project.gollazoom.wears.controller;

import gdg.hongik.project.gollazoom.wears.service.WearRecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/closet/wears")
public class WearRecommendController {
    private final WearRecommendService wearRecommendService;
}
