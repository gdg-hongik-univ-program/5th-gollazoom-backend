package gdg.hongik.project.gollazoom.closet.dto;

import gdg.hongik.project.gollazoom.closet.entity.WashStatus;

// 단일 변경
public record ClothWashStatusUpdateRequest(
        WashStatus washStatus
) {}
