package gdg.hongik.project.gollazoom.wears.dto;

import java.time.LocalDate;
import java.util.List;

public record WearUpdateRequest(
        LocalDate date,
        List<Long> clothIds
) { }
