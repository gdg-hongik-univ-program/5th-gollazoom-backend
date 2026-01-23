package gdg.hongik.project.gollazoom.wears.service;

import gdg.hongik.project.gollazoom.closet.entity.Category;
import gdg.hongik.project.gollazoom.closet.entity.Cloth;
import gdg.hongik.project.gollazoom.closet.repository.ClothRepository;
import gdg.hongik.project.gollazoom.user.entity.User;
import gdg.hongik.project.gollazoom.user.repository.UserRepository;
import gdg.hongik.project.gollazoom.wears.dto.WearRecommendResponse;
import gdg.hongik.project.gollazoom.wears.entity.Wear;
import gdg.hongik.project.gollazoom.wears.repository.WearRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WearRecommendServiceImpl implements  WearRecommendService{
    // 점수 정책, 수정될 수 있음.
    private static final int BASE_SCORE = 100;
    private static final int PENALTY_RAIN = 30;
    private static final int PENALTY_TEMP = 60;
    private static final int PENALTY_YESTERDAY = 90;

    private final UserRepository userRepository;
    private final ClothRepository clothRepository;
    private final WearRepository wearRepository;

    /*
    private final OutfitService outfitService;
    private final WeatherService weatherService;
    */

    @Override
    public WearRecommendResponse recommend(String username, LocalDate date) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 1. 날씨 조회 - 구현 시 연결
        /*
        WeatherService.WeatherInfo weather = safeGetWeather(date);
        boolean isRaining = weather.isRaining();
        double temperature = weather.temperature();
         */

        // 2. 어제 착용한 옷 조회
        Set<Long> yesterdayClothIds = wearRepository.findByUser_IdAndDate(user.getId(), date.minusDays(1))
                .map(this::extractClothIds)
                .orElseGet(Collections::emptySet);

        // 3. 내 옷 전체 로딩 후 카테고리 분류
        List<Cloth> allClothes = clothRepository.findAllByUser_IdOrderByCreatedAtDesc(user.getId());

        List<Cloth> dresses = filterByCategory(allClothes, Category.DRESS);
        List<Cloth> tops = filterByCategory(allClothes, Category.TOP);
        List<Cloth> bottoms = filterByCategory(allClothes, Category.BOTTOM);
        List<Cloth> outers = filterByCategory(allClothes, Category.OUTER);

        List<WearRecommendResponse.Recommendation> candidates = new ArrayList<>();

        // 프리셋 코디 1순위. 단, 하나라도 위반되면 고려하지 않는다.
        List<OutfitService.OutfitDto> presets = outfitService.listMyOutfits(username);
        for (OutfitService.OutfitDto preset : presets) {
            List<Long> clothIds = preset.clothIds();

            // 1. 프리셋 옷들이 전부 내 옷인지 검증과 동시에 엔티티 확보
            List<Cloth> presetClothes = clothRepository.findAllByIdInAndUser_Id(clothIds, user.getId());
            if (presetClothes.size() != clothIds.size()) continue;

            // 2. 원피스 조합 검증
            if (!isValidCombination(presetClothes)) continue;

            // 3. 조건 위반 체크 (하나라도 위반될 시 프리셋은 더 이상 따지지 않는다.
            if (violatesRainRule(presetClothes, isRaining)) continue;
            if (violatesTemperatureRule(presetClothes, temperature)) continue;
            if (violateTesterdayRule(presetClothes, yesterdayClothIds)) continue;

            // 4. 통과 시 무조건 추천
            candidates.add(new WearRecommendResponse.Recommendation(
                    "PRESET",
                    100, // 만점!
                    List.of(),
                    clothIds
            ));
        }

        // 프리셋이 만족하지 않으면 continue로 인해 여기로 오게 됨.

        // 1. DRESS 단일 코디 후보
        for (Cloth d : dresses) {
            candidates.add(scoreCandidate("DRESS", List.of(d), isRaining, temperature, yesterdayClothIds));
        }

        // 2. TOP + BOTTOM 후보
        List<Cloth> topPool = tops.stream().limit(10).toList(); // 10개만 받아옴
        List<Cloth> bottomPool = bottoms.stream().limit(10).toList();

        for (Cloth t : topPool) {
            for (Cloth b : bottomPool) {
                candidates.add(scoreCandidate("TOP_BOTTOM", List.of(t, b), isRaining, temperature, yesterdayClothIds));
            }
        }

        // 3. OUTER 추가 여부

        // 4. 정렬 및 상위 N개 반환
        List<WearRecommendResponse.Recommendation> top = candidates.stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(WearRecommendResponse.Recommendation::score).reversed())
                .limit(3) // 일단 상위 3개만 반환, 사실 1개만 반환해도 됨. (강제성)
                .toList();

        return new WearRecommendResponse(date, isRaining, temperature, top);
    }

    // 점수 계산 로직
    private WearRecommendResponse.Recommendation scoreCandidate(
            String type,
            List<Cloth> clothes,
            boolean isRaining,
            double temperature,
            Set<Long> yesterdayClothIds
    ) {
        if (!isValidCombination(clothes)) return null;

        int score = BASE_SCORE;
        List<String> warnings = new ArrayList<>(); // 디버깅

        // 1. 비
        if (isRaining && violatesRainRule(clothes, true)) {
            score -= PENALTY_RAIN;
            warnings.add("isRaining 위배");
        }

        // 2. 온도
        if (violatesTemperatureRule(clothes, temperature)) {
            score -= PENALTY_TEMP;
            warnings.add("온도에 맞지 않는 옷일 가능성");
        }

        // 3. 어제 입은 옷
        if (violatesYesterdayRule(clothes, yesterdayClothIds)) {
            score -= PENALTY_YESTERDAY;
            warnings.add("어제 입은 옷이 포함되어 있음");
        }

        // score 하한선은 일단 마이너스 허용

        List<Long> clothIds = clothes.stream().map(Cloth::getId).toList();

        return new WearRecommendResponse.Recommendation(type, score, warnings, clothIds);
    }

    // 룰 체크 로직
    // 1. DRESS + TOP/BOTTOM 혼합 금지
    private boolean isValidCombination(List<Cloth> clothes) {
        boolean hasDress = clothes.stream().anyMatch(c -> c.getCategory() == Category.DRESS);
        boolean hasTop = clothes.stream().anyMatch(c -> c.getCategory() == Category.TOP);
        boolean hasBottom = clothes.stream().anyMatch(c -> c.getCategory() == Category.BOTTOM);

        return !(hasDress && (hasTop || hasBottom));
    }

    // 2. 비 오는 날 isRaining=false 옷이 포함되면 위반
    private boolean violatesRainRule(List<Cloth> clothes, boolean isRaining) {
        if (!isRaining) return false;

        return clothes.stream().anyMatch(c -> !c.isRaining());
    }

    // 3. 온도 룰 - OUTER 정책이랑 같이 짜야 해서 일단 여기까지
    private boolean violatesTemperatureRule(List<Cloth> clothes, double temperature) {
        return false;
    }

    // 4. 어제 입은 옷 룰
    private boolean violatesYesterdayRule(List<Cloth> clothes, Set<Long> yesterdayClothIds) {
        return clothes.stream()
                .anyMatch(c -> yesterdayClothIds.contains(c.getId()));
    }

    private Set<Long> extractClothIds(Wear wear) {
        return wear.getItems().stream()
                .map(wi -> wi.getCloth().getId())
                .collect(Collectors.toSet());
    }

    private List<Cloth> filterByCategory(List<Cloth> all, Category category) {
        return all.stream().filter(c -> c.getCategory() == category).toList();
    }

    private WeatherService.WeatherInfo safeGetWeather(LocalDate date) {
        try {
            return weatherService.getWeather(date);
        } catch (Exception e) {
            return new WeatherService.WeatherInfo(false, 20.0); // 연결 실패 시 20도로 설정됨.
        }
    }

}
