package gdg.hongik.project.gollazoom.weather.controller;

import gdg.hongik.project.gollazoom.weather.dto.response.WeatherTodayResponse;
import gdg.hongik.project.gollazoom.weather.dto.response.WeatherTodaySummaryResponse;
import gdg.hongik.project.gollazoom.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/weather")
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping("/today")
    public ResponseEntity<WeatherTodayResponse> getTodayWeather() {
        return ResponseEntity.ok(weatherService.getTodayForecast());
    }

    @GetMapping("/today/summary")
    public ResponseEntity<WeatherTodaySummaryResponse> getTodaySummary() {
        return ResponseEntity.ok(weatherService.getTodaySummary());
    }
}