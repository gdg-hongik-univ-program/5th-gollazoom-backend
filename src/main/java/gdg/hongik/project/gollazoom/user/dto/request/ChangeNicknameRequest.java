package gdg.hongik.project.gollazoom.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ChangeNicknameRequest(
        @NotBlank(message = "no nickname!")
        String nickname
) {
}
