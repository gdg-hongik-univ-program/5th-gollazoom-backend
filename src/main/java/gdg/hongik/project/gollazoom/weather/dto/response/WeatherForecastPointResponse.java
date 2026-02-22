package gdg.hongik.project.gollazoom.weather.dto.response;

public record WeatherForecastPointResponse(
        String time,   // "0900"
        Integer tmp,   // TMP
        Integer pop,   // POP
        Integer pty,   // PTY
        Integer sky    // SKY
) {
}
