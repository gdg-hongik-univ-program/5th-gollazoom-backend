package gdg.hongik.project.gollazoom.user.dto.request;

public record SignupRequest( // 필드, 생성자, getter 자동생성
        String username,
        String password,
        String nickname
) {
}
