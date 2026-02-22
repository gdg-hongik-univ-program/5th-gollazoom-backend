package gdg.hongik.project.gollazoom.user.dto.response;

public record UserWashSettingResponse(
        boolean isUsingWashUpTech
) {
    public static UserWashSettingResponse from(boolean v) {
        return new UserWashSettingResponse(v);
    }
}