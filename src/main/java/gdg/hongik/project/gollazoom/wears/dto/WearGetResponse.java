package gdg.hongik.project.gollazoom.wears.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record WearGetResponse(
        Long wearId,
        LocalDate date,
        List<Long> clothIds,
        String memo, // 이 메모를 나중에 OUTFIT API에서 불러올 듯
        LocalDateTime createdAt
) { }
