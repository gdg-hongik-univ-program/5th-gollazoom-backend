package gdg.hongik.project.gollazoom.wears.service;

import gdg.hongik.project.gollazoom.wears.dto.WearRecommendResponse;

import java.time.LocalDate;

public interface WearRecommendService {
    WearRecommendResponse recommend(String username, LocalDate date);
}
