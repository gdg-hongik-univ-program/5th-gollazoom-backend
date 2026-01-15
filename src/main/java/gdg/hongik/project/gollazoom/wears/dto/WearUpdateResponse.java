package gdg.hongik.project.gollazoom.wears.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record WearUpdateResponse(
        Long wearId,
        LocalDate date,
        List<Long> clothIds,
        LocalDateTime updatedAt
) { }
