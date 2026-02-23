package gdg.hongik.project.gollazoom.wears.service;

import gdg.hongik.project.gollazoom.closet.entity.Category;
import gdg.hongik.project.gollazoom.closet.entity.Cloth;
import gdg.hongik.project.gollazoom.closet.entity.Season;
import gdg.hongik.project.gollazoom.closet.entity.WashStatus;
import gdg.hongik.project.gollazoom.closet.repository.ClothRepository;
import gdg.hongik.project.gollazoom.presets.dto.response.PresetDetailResponse;
import gdg.hongik.project.gollazoom.presets.dto.response.PresetItemDetailResponse;
import gdg.hongik.project.gollazoom.presets.dto.response.PresetListResponse;
import gdg.hongik.project.gollazoom.presets.service.PresetService;
import gdg.hongik.project.gollazoom.user.entity.User;
import gdg.hongik.project.gollazoom.user.repository.UserRepository;
import gdg.hongik.project.gollazoom.wears.dto.WearRecommendResponse;
import gdg.hongik.project.gollazoom.wears.entity.Wear;
import gdg.hongik.project.gollazoom.wears.repository.WearRepository;
import gdg.hongik.project.gollazoom.weather.dto.response.WeatherTodaySummaryResponse;
import gdg.hongik.project.gollazoom.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.format.DateTimeFormatter;

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

    // 아우터 추천 정책
    private static final double OUTER_CUTOFF_TEMP = 25.0; // 너무 덥나?

    private final UserRepository userRepository;
    private final ClothRepository clothRepository;
    private final WearRepository wearRepository;


    private final PresetService presetService;
    private final WeatherService weatherService;

    private WeatherTodaySummaryResponse safeGetWeatherSummary(LocalDate date) {
        try {
            // 현재 오늘 기준 날짜만 제공하고 있음
            if (LocalDate.now().equals(date)) {
                return weatherService.getTodaySummary();
            }

            // 따라서 오늘이 아닌 날짜의 날씨는 일단 평균 날씨로 가정하겠음.
            return new WeatherTodaySummaryResponse(
                    date.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                    20.0,
                    false
            );

        } catch (Exception e) {
            // API 실패 시
            return new WeatherTodaySummaryResponse(
                    date.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                    20.0,
                    false
            );
        }
    }


    @Override
    public WearRecommendResponse recommend(Long userId, LocalDate date) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 1. 날씨 조회 - 구현 시 연결

        WeatherTodaySummaryResponse weather = safeGetWeatherSummary(date);

        boolean isRaining = weather.hasRainOrSnow();
        double temperature = weather.avgTemp();


        // 2. 어제 착용한 옷 조회
        Set<Long> yesterdayClothIds = wearRepository.findByUser_IdAndDate(user.getId(), date.minusDays(1))
                .map(this::extractClothIds)
                .orElseGet(Collections::emptySet);

        // 3. 내 옷 전체 로딩 후 카테고리 분류
        List<Cloth> allClothes = clothRepository.findAllByUser_IdOrderByCreatedAtDesc(user.getId());
        // 정책 추가 : 세탁중이 아닌 옷만 우선적으로 추천 대상 옷에 포함시킨다.
        List<Cloth> usableClothes = allClothes.stream()
                .filter(c -> c.getWashStatus() != WashStatus.WASHING)
                .toList();

        List<WearRecommendResponse.Recommendation> candidates = new ArrayList<>();

        // 프리셋 코디 1순위. 단, 하나라도 위반되면 고려하지 않는다.
        List<PresetListResponse> presets = presetService.getPresetList(user.getId());
        for (PresetListResponse preset : presets) {
            PresetDetailResponse detail = presetService.getPresetDetail(user.getId(), preset.presetId());

            List<Long> clothIds = detail.items().stream()
                    .map(PresetItemDetailResponse::clothId)
                    .toList();

            // 1. 프리셋 옷들이 전부 내 옷인지 검증과 동시에 엔티티 확보
            List<Cloth> presetClothes = clothRepository.findAllByIdInAndUser_Id(clothIds, user.getId());
            if (presetClothes.size() != clothIds.size()) continue;

            // 2. 원피스 조합 검증
            if (!isValidCombination(presetClothes)) continue;

            // 3. 조건 위반 체크 (하나라도 위반될 시 프리셋은 더 이상 따지지 않는다.
            if (presetClothes.stream().anyMatch(c -> c.getWashStatus() == WashStatus.WASHING)) continue;
            if (violatesRainRule(presetClothes, isRaining)) continue;
            if (violatesTemperatureRule(presetClothes, temperature)) continue;
            if (violatesYesterdayRule(presetClothes, yesterdayClothIds)) continue;

            List<WearRecommendResponse.ClothSummary> summaries = presetClothes.stream()
                    .map(c -> new WearRecommendResponse.ClothSummary(
                            c.getId(),
                            c.getImageUrl(),
                            c.getCategory().name(),
                            c.getSubCategory() == null ? null : c.getSubCategory().name(),
                            c.getColorCode(),
                            c.isRaining()
                    ))
                    .toList();

            // 4. 통과 시 무조건 추천
            candidates.add(new WearRecommendResponse.Recommendation(
                    "PRESET",
                    100, // 만점!
                    List.of(),
                    summaries
            ));
        }

        // 프리셋이 만족하지 않으면 continue로 인해 여기로 오게 됨.

        List<Cloth> dresses = filterByCategory(usableClothes, Category.DRESS);
        List<Cloth> tops = filterByCategory(usableClothes, Category.TOP);
        List<Cloth> bottoms = filterByCategory(usableClothes, Category.BOTTOM);
        final List<Cloth> primaryOuters = filterByCategory(usableClothes, Category.OUTER);
        List<Cloth> fallbackOuters = primaryOuters;

        // 1. DRESS 단일 코디 후보
        for (Cloth d : dresses) {
            WearRecommendResponse.Recommendation base =
                scoreCandidate("DRESS", List.of(d), isRaining, temperature, yesterdayClothIds);
            if (base != null) candidates.add(base);
        }

        // 2. TOP + BOTTOM 후보
        List<Cloth> topPool = tops.stream().limit(10).toList(); // 10개만 받아옴
        List<Cloth> bottomPool = bottoms.stream().limit(10).toList();

        for (Cloth t : topPool) {
            for (Cloth b : bottomPool) {
                WearRecommendResponse.Recommendation base =
                    scoreCandidate("TOP_BOTTOM", List.of(t, b), isRaining, temperature, yesterdayClothIds);
                if (base != null) candidates.add(base);
            }
        }
        // 3. 후보 부족 시 세탁 중 옷 포함해서 다시 후보 추가 + 경고 메시지 반환
        if (candidates.size() < 3) {
            addFallbackCandidates(
                    candidates,
                    filterByCategory(allClothes, Category.DRESS),
                    filterByCategory(allClothes, Category.TOP),
                    filterByCategory(allClothes, Category.BOTTOM),
                    isRaining, temperature, yesterdayClothIds
            );
            // 조합 폭발 막기위해 outer 따로 처리
            fallbackOuters = filterByCategory(allClothes, Category.OUTER);
        }

        // toList 불변 관련 컴파일 에러를 막기 위해 생성.
        final List<Cloth> finalOuters = fallbackOuters;

        // 4. OUTER 추가 여부
        List<WearRecommendResponse.Recommendation> withOuter = candidates.stream()
                .map(base -> applyOuterPolicy(base, finalOuters, isRaining, temperature, yesterdayClothIds))
                .toList();


        // 5. 정렬 및 상위 N개 반환
        List<WearRecommendResponse.Recommendation> top = withOuter.stream()
                .sorted(Comparator.comparingInt(WearRecommendResponse.Recommendation::score).reversed())
                .limit(3) // 일단 상위 3개만 반환, 사실 1개만 반환해도 됨. (강제성)
                .toList();

        return new WearRecommendResponse(date, isRaining, temperature, top);
    }

    // fallback 후보 추가
    private void addFallbackCandidates(
            List<WearRecommendResponse.Recommendation> candidates,
            List<Cloth> dresses,
            List<Cloth> tops,
            List<Cloth> bottoms,
            boolean isRaining,
            double temperature,
            Set<Long> yesterdayClothIds
    ) {
        for (Cloth d : dresses) {
            WearRecommendResponse.Recommendation r =
                    scoreCandidate("DRESS", List.of(d), isRaining, temperature, yesterdayClothIds);
            if (r != null) candidates.add(addWashFallbackWarningIfNeeded(r, List.of(d)));
        }

        List<Cloth> topPool = tops.stream().limit(10).toList();
        List<Cloth> bottomPool = bottoms.stream().limit(10).toList();

        for (Cloth t : topPool) {
            for (Cloth b : bottomPool) {
                List<Cloth> combo = List.of(t, b);
                WearRecommendResponse.Recommendation r =
                        scoreCandidate("TOP_BOTTOM", combo, isRaining, temperature, yesterdayClothIds);
                if (r != null) candidates.add(addWashFallbackWarningIfNeeded(r, combo));
            }
        }
    }

    private WearRecommendResponse.Recommendation addWashFallbackWarningIfNeeded(
            WearRecommendResponse.Recommendation base,
            List<Cloth> clothes
    ) {
        boolean hasWashing = clothes.stream().anyMatch(c -> c.getWashStatus() == WashStatus.WASHING);
        if (!hasWashing) return base;

        List<String> warnings = new ArrayList<>(base.warnings());
        warnings.add("추천 코디 후보가 부족하여 세탁중인 의상이 포함될 수 있어요.");

        return new WearRecommendResponse.Recommendation(
                base.type(),
                base.score(),
                warnings,
                base.clothes()
        );
    }

    // 제외 디버깅용
    private WearRecommendResponse.Recommendation appendWarning(
            WearRecommendResponse.Recommendation base,
            String warning
    ) {
        List<String> newWarnings = new ArrayList<>(base.warnings());
        newWarnings.add(warning);

        return new WearRecommendResponse.Recommendation(
                base.type(),
                base.score(),
                newWarnings,
                base.clothes()
        );
    }

    // OUTER 관련 정책, 조합 수를 줄이기 위해 기본 base에 최고 점수 받은 아우터 합체
    private WearRecommendResponse.Recommendation applyOuterPolicy(
            WearRecommendResponse.Recommendation base,
            List<Cloth> outers,
            boolean isRaining,
            double temperature,
            Set<Long> yesterdayClothIds
    ) {
        if (base == null) return null;

        // 25도 이상이면 너무 더우니 아우터 제외.
        if (temperature >= OUTER_CUTOFF_TEMP) {
            return appendWarning(base, "온도가 높아 아우터 제외");
        }

        if (outers == null || outers.isEmpty()) return base;

        Cloth outerCandidate = null;
        int bestOuterScore = Integer.MIN_VALUE;
        List<String> outerWarnings = List.of();

        for (Cloth o : outers.stream().limit(10).toList()) {
            WearRecommendResponse.Recommendation scoredOuter =
                    scoreCandidate("OUTER", List.of(o), isRaining, temperature, yesterdayClothIds);
            if (scoredOuter == null) continue;

            if (scoredOuter.score() > bestOuterScore) {
                bestOuterScore = scoredOuter.score();
                outerCandidate = o;
                outerWarnings = scoredOuter.warnings();
            }
        }
        if (outerCandidate == null) return base;

        // 합체, base clothIds + outerId
        List<WearRecommendResponse.ClothSummary> mergedClothes =
                new ArrayList<>(base.clothes());

        mergedClothes.add(
                new WearRecommendResponse.ClothSummary(
                        outerCandidate.getId(),
                        outerCandidate.getImageUrl(),
                        outerCandidate.getCategory().name(),
                        outerCandidate.getSubCategory() == null ? null : outerCandidate.getSubCategory().name(),
                        outerCandidate.getColorCode(),
                        outerCandidate.isRaining()
                )
        );

        // 점수 합산(디버깅용)
        int mergedScore = base.score() + bestOuterScore;
        List<String> mergedWarnings = new ArrayList<>(base.warnings());

        // 감점 사유도 포함하여 전송
        mergedWarnings.addAll(outerWarnings);

        return new WearRecommendResponse.Recommendation(
                base.type(),
                mergedScore,
                mergedWarnings,
                mergedClothes
        );
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

        List<WearRecommendResponse.ClothSummary> summaries = clothes.stream()
                .map(c -> new WearRecommendResponse.ClothSummary(
                        c.getId(),
                        c.getImageUrl(),
                        c.getCategory().name(),
                        c.getSubCategory() == null ? null : c.getSubCategory().name(),
                        c.getColorCode(),
                        c.isRaining()
                ))
                .toList();

        return new WearRecommendResponse.Recommendation(type, score, warnings, summaries);
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

    // 3. 온도 룰
    // 3-1. 온도에 따른 계절 분류. 온도는... 일단 내 기준
    private EnumSet<Season> allowedSeasons(double temperature) {
        if (temperature < 7.0) {
            return EnumSet.of(Season.WINTER);
        }
        if (temperature >= 22.0) {
            return EnumSet.of(Season.SUMMER);
        }
        return EnumSet.of(Season.SPRING, Season.FALL);
    }

    // 3-2. 온도 룰 위반 여부: 시즌이 맞는지?
    private boolean violatesTemperatureRule(List<Cloth> clothes, double temperature) {
        EnumSet<Season> allowed = allowedSeasons(temperature);

        return clothes.stream().anyMatch(c -> {
            Season s = c.getSeason();

            if (s == null) return false; // 시즌 태그가 없으면 일단 패스

            if (s == Season.ALL) return false; // 사계절 옷은 일단 패스

            return !allowed.contains(s); // allowed 아니면 위반이다.
        });

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

}
