package gdg.hongik.project.gollazoom.user.dto.request;

public record ChangePasswordRequest(
        String currentPassword,
        String newPassword
) {}