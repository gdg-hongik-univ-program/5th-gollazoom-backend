package gdg.hongik.project.gollazoom.closet.service;

import gdg.hongik.project.gollazoom.closet.dto.ClosetCreateRequest;
import gdg.hongik.project.gollazoom.closet.dto.ClosetItemListResponse;
import gdg.hongik.project.gollazoom.closet.dto.ClosetResponse;
import gdg.hongik.project.gollazoom.closet.dto.ClosetUpdateRequest;

import java.util.List;

public interface ClosetService {
    ClosetResponse create(Long userId, ClosetCreateRequest request);
    List<ClosetItemListResponse> list(Long userId);
    ClosetResponse get(Long userId, Long clothId);
    ClosetResponse update(Long userId, Long clothId, ClosetUpdateRequest request);
    void delete(Long userId, Long clothId);
}
