package gdg.hongik.project.gollazoom.wears.controller;

import gdg.hongik.project.gollazoom.global.api.ApiResponse;
import gdg.hongik.project.gollazoom.wears.dto.WearCreateRequest;
import gdg.hongik.project.gollazoom.wears.dto.WearCreateResponse;
import gdg.hongik.project.gollazoom.wears.service.WearService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/wears")
public class WearController {

    private final WearService wearService;

    @PostMapping
    public ResponseEntity<ApiResponse<WearCreateResponse>> create(@RequestBody WearCreateRequest request) {
        WearCreateResponse data = wearService.create(request);

        return ResponseEntity
                .created(URI.create("/api/wears/" + data.wearId()))
                .body(ApiResponse.ok("착용 계획이 저장되었어요.", data));
    }
}
