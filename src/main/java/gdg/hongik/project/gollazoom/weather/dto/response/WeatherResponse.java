package gdg.hongik.project.gollazoom.weather.dto.response;

public record WeatherResponse(
        double temperature,
        double precipitation,
        boolean isRaining
) {
}
