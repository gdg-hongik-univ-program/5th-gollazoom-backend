package gdg.hongik.project.gollazoom.weather.service;

import gdg.hongik.project.gollazoom.weather.dto.response.WeatherForecastPointResponse;
import gdg.hongik.project.gollazoom.weather.dto.response.WeatherTodayResponse;
import gdg.hongik.project.gollazoom.weather.dto.response.WeatherTodaySummaryResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class WeatherService {

    // 서울좌표
    private static final int SEOUL_NX = 60;
    private static final int SEOUL_NY = 127;

    @Value("${weather.kma.service-key}")
    private String serviceKey;

    private final RestClient restClient = RestClient.create();

    /**
     * 서울 "오늘" 단기예보(미래 예보 포함) 반환
     * - 기상청 getVilageFcst 호출
     * - fcstDate == 오늘인 것만 필터
     * - 시간대별(TMP/POP/PTY/SKY) 리스트 반환
     */
    public WeatherTodayResponse getTodayForecast() {
        return getTodayForecastInternal(SEOUL_NX, SEOUL_NY);
    }

    private WeatherTodayResponse getTodayForecastInternal(int nx, int ny) {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        BaseInfo base = chooseBaseInfo(); // base_date/base_time 자동 선택
        List<Map<String, Object>> items = callVilageFcst(nx, ny, base.baseDate(), base.baseTime());

        // fcstTime -> (category -> value)
        Map<String, Map<String, String>> byTime = new HashMap<>();

        for (Map<String, Object> it : items) {
            String fcstDate = String.valueOf(it.get("fcstDate"));
            if (!today.equals(fcstDate)) continue;

            String fcstTime = String.valueOf(it.get("fcstTime"));
            String category = String.valueOf(it.get("category"));
            String fcstValue = String.valueOf(it.get("fcstValue"));

            byTime.computeIfAbsent(fcstTime, k -> new HashMap<>())
                    .put(category, fcstValue);
        }

        List<String> times = new ArrayList<>(byTime.keySet());
        Collections.sort(times);

        List<WeatherForecastPointResponse> forecasts = new ArrayList<>();
        for (String time : times) {
            Map<String, String> m = byTime.get(time);

            forecasts.add(new WeatherForecastPointResponse(
                    time,
                    parseIntOrNull(m.get("TMP")),
                    parseIntOrNull(m.get("POP")),
                    parseIntOrNull(m.get("PTY")),
                    parseIntOrNull(m.get("SKY"))
            ));
        }

        return new WeatherTodayResponse(today, forecasts);
    }

    private Integer parseIntOrNull(String v) {
        if (v == null) return null;
        try {
            // 가끔 "3.0"처럼 올 수 있어서 Double 파싱 후 int 변환
            return (int) Double.parseDouble(v);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 단기예보 발표시각 자동 선택
     * 가능한 base_time: 0200,0500,0800,1100,1400,1700,2000,2300
     *
     * 안전하게 "현재 - 10분" 기준으로, 이미 발표된 가장 최신 base_time을 고른다.
     * 오늘 새벽처럼 아직 오늘 발표가 없을 때는 전날 2300 사용.
     */
    private BaseInfo chooseBaseInfo() {
        List<String> baseTimes = List.of("2300", "2000", "1700", "1400", "1100", "0800", "0500", "0200");

        LocalDateTime now = LocalDateTime.now().minusMinutes(10);
        LocalDate date = now.toLocalDate();
        LocalTime time = now.toLocalTime();

        for (String t : baseTimes) {
            LocalTime bt = LocalTime.of(Integer.parseInt(t.substring(0, 2)), Integer.parseInt(t.substring(2, 4)));
            if (!bt.isAfter(time)) {
                return new BaseInfo(date.format(DateTimeFormatter.ofPattern("yyyyMMdd")), t);
            }
        }

        // 오늘 발표가 하나도 없으면 전날 2300 사용
        LocalDate yesterday = date.minusDays(1);
        return new BaseInfo(yesterday.format(DateTimeFormatter.ofPattern("yyyyMMdd")), "2300");
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> callVilageFcst(int nx, int ny, String baseDate, String baseTime) {

        String host = "https://apis.data.go.kr";
        String path = "/1360000/VilageFcstInfoService_2.0/getVilageFcst";

        RestClient client = RestClient.create(host);

        Map<String, Object> root = client.get()
                .uri(uriBuilder -> uriBuilder
                        .path(path)
                        .queryParam("serviceKey", serviceKey)
                        .queryParam("numOfRows", 1000)
                        .queryParam("pageNo", 1)
                        .queryParam("dataType", "JSON")
                        .queryParam("base_date", baseDate)
                        .queryParam("base_time", baseTime)
                        .queryParam("nx", nx)
                        .queryParam("ny", ny)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        Map<String, Object> response = (Map<String, Object>) root.get("response");
        Map<String, Object> header = (Map<String, Object>) response.get("header");

        String resultCode = String.valueOf(header.get("resultCode"));
        if (!"00".equals(resultCode)) {
            String resultMsg = String.valueOf(header.get("resultMsg"));
            throw new IllegalStateException("KMA API error: " + resultCode + " / " + resultMsg);
        }

        Map<String, Object> body = (Map<String, Object>) response.get("body");
        Map<String, Object> items = (Map<String, Object>) body.get("items");
        return (List<Map<String, Object>>) items.get("item");
    }

    private record BaseInfo(String baseDate, String baseTime) {}

    public WeatherTodaySummaryResponse getTodaySummary() {
        WeatherTodayResponse today = getTodayForecast(); // 기존 오늘 예보

        double sum = 0.0;
        int count = 0;

        boolean hasRainOrSnow = false;

        for (var f : today.forecasts()) {
            if (f.tmp() != null) {
                sum += f.tmp();
                count++;
            }
            // PTY: 0 아니면 비/눈/소나기
            if (f.pty() != null && f.pty() != 0) {
                hasRainOrSnow = true;
            }
        }

        double avg = (count == 0) ? 0.0 : sum / count;
        avg = Math.round(avg * 10.0) / 10.0;

        return new WeatherTodaySummaryResponse(
                today.date(),
                avg,
                hasRainOrSnow
        );
    }
}