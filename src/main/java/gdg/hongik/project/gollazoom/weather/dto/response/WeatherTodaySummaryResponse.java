package gdg.hongik.project.gollazoom.weather.dto.response;

public record WeatherTodaySummaryResponse(
        String date,
        double avgTemp,
        boolean hasRainOrSnow
) {
}
