package gdg.hongik.project.gollazoom.closet.service;

import gdg.hongik.project.gollazoom.closet.dto.*;
import gdg.hongik.project.gollazoom.closet.entity.WashStatus;

import java.util.List;

public interface ClosetService {
    ClosetResponse create(Long userId, ClosetCreateRequest request);
    List<ClosetItemListResponse> list(Long userId, WashStatus washStatus);
    ClosetResponse get(Long userId, Long clothId);
    ClosetResponse update(Long userId, Long clothId, ClosetUpdateRequest request);
    void delete(Long userId, Long clothId);
    void updateWashStatus(Long userId, Long clothId, ClothWashStatusUpdateRequest request);
    void updateWashStatusBulk(Long userId, ClothWashStatusBulkUpdateRequest request);
}
