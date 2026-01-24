package gdg.hongik.project.gollazoom.presets.controller;

import gdg.hongik.project.gollazoom.presets.dto.request.PresetCreateRequest;
import gdg.hongik.project.gollazoom.presets.dto.response.PresetCreateResponse;
import gdg.hongik.project.gollazoom.presets.dto.response.PresetDetailResponse;
import gdg.hongik.project.gollazoom.presets.entity.Preset;
import gdg.hongik.project.gollazoom.presets.service.PresetService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/presets")
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
}
