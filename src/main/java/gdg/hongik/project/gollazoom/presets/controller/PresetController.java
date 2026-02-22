package gdg.hongik.project.gollazoom.presets.controller;

import gdg.hongik.project.gollazoom.global.api.ApiResponse;
import gdg.hongik.project.gollazoom.presets.dto.request.PresetCreateRequest;
import gdg.hongik.project.gollazoom.presets.dto.request.PresetUpdateRequest;
import gdg.hongik.project.gollazoom.presets.dto.response.PresetCreateResponse;
import gdg.hongik.project.gollazoom.presets.dto.response.PresetDetailResponse;
import gdg.hongik.project.gollazoom.presets.dto.response.PresetListResponse;
import gdg.hongik.project.gollazoom.presets.dto.response.PresetWashCheckResponse;
import gdg.hongik.project.gollazoom.presets.service.PresetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/presets")
public class PresetController {
    private final PresetService presetService;

    @PostMapping
    public ResponseEntity<PresetCreateResponse> createPreset(@RequestBody PresetCreateRequest request) {
        Long userId = (Long) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        return ResponseEntity.ok(presetService.createPreset(userId, request));
    }

    @GetMapping("/{presetId}")
    public ResponseEntity<PresetDetailResponse> getPresetDetail(@PathVariable Long presetId) {
        Long userId = (Long) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        return ResponseEntity.ok(presetService.getPresetDetail(userId, presetId));
    }

    @GetMapping("/{presetId}/washCheck")
    public ApiResponse<PresetWashCheckResponse> washCheck(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long presetId
    ) {
        PresetWashCheckResponse data = presetService.washCheck(userId, presetId);
        return ApiResponse.ok(null, data);
    }

    @GetMapping
    public ResponseEntity<List<PresetListResponse>> getPresetList() {
        Long userId = (Long) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        return ResponseEntity.ok(presetService.getPresetList(userId));
    }

    @PatchMapping("/{presetId}")
    public ResponseEntity<Void> updatePreset(
            @PathVariable Long presetId,
            @RequestBody PresetUpdateRequest request
    ){
        Long  userId = (Long) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        presetService.updatePreset(userId, presetId, request);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/{presetId}")
    public ResponseEntity<Void> deletePreset(@PathVariable Long presetId) {
        Long userId = (Long) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        presetService.deletePreset(userId, presetId);
        return ResponseEntity.noContent().build();
    }
}
