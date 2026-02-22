package gdg.hongik.project.gollazoom.closet.dto;

import gdg.hongik.project.gollazoom.closet.entity.WashStatus;

import java.util.List;

// 일괄 변경
public record ClothWashStatusBulkUpdateRequest(
        List<Long> clothIds,
        WashStatus washStatus
) {
}
