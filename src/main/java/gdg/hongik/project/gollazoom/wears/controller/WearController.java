package gdg.hongik.project.gollazoom.wears.controller;

import gdg.hongik.project.gollazoom.global.api.ApiResponse;
import gdg.hongik.project.gollazoom.wears.dto.*;
import gdg.hongik.project.gollazoom.wears.service.WearService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wears")
public class WearController {

    private final WearService wearService;

    @PostMapping
    public ResponseEntity<ApiResponse<WearCreateResponse>> create(@RequestBody WearCreateRequest request) {
        WearCreateResponse data = wearService.create(request);

        return ResponseEntity
                .created(URI.create("/api/wears/" + data.wearId()))
                .body(ApiResponse.ok("착용 계획이 저장되었어요.", data));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<WearGetResponse>> getByDate(@RequestParam LocalDate date) {
        Optional<WearGetResponse> result = wearService.getByDate(date);
        if (result.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.ok("해당 날짜의 착용 기록이 없어요.", null));
        }

        return ResponseEntity.ok(ApiResponse.ok("착용 기록을 불러왔어요.", result.get()));
    }

    @PatchMapping("/{wearId}")
    public ResponseEntity<ApiResponse<WearUpdateResponse>> update(
            @PathVariable Long wearId,
            @RequestBody WearUpdateRequest request
    ) {
        WearUpdateResponse data = wearService.update(wearId, request);
        return ResponseEntity.ok(ApiResponse.ok("착용 기록이 수정되었어요.", data));
    }

    @DeleteMapping("/{wearId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long wearId) {
        wearService.delete(wearId);
    }
}
