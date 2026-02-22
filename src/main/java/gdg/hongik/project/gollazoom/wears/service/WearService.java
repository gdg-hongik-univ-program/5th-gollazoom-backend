package gdg.hongik.project.gollazoom.wears.service;

import gdg.hongik.project.gollazoom.wears.dto.*;

import java.time.LocalDate;
import java.util.Optional;

public interface WearService {
    WearCreateResponse create(WearCreateRequest request);
    Optional<WearGetResponse> getByDate(LocalDate date);
    WearUpdateResponse update(Long wearId, WearUpdateRequest request);
    void delete(Long wearId);
}
