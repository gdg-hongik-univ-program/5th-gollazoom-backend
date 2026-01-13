package gdg.hongik.project.gollazoom.wears.service;

import gdg.hongik.project.gollazoom.wears.dto.WearCreateRequest;
import gdg.hongik.project.gollazoom.wears.dto.WearCreateResponse;

public interface WearService {
    WearCreateResponse create(WearCreateRequest request);
}
