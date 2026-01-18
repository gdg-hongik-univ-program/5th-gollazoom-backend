package gdg.hongik.project.gollazoom.user.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record WorktimeRequest(
        @NotNull
        @Pattern(
                regexp = "^([01]\\d|2[0-3]):[0-5]\\d$",
                message = "HH:mm 형식이어야 합니다 (예: 08:00)"
        )
        String worktime
) {
}
