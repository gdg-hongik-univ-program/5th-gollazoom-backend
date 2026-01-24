package gdg.hongik.project.gollazoom.weather.dto.response;

import java.util.List;

public record WeatherTodayResponse(
        String date,
        List<WeatherForecastPointResponse> forecasts
) {
}
