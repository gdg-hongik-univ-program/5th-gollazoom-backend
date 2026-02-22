package gdg.hongik.project.gollazoom.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record SignupRequest( // 필드, 생성자, getter 자동생성
                             @Schema(description = "아이디")
                             @NotBlank
                             String username,
                             @Schema(description = "비밀번호")
                             @NotBlank
                             String password,
                             @Schema(description = "닉네임")
                             @NotBlank
                             String nickname
) {
}
