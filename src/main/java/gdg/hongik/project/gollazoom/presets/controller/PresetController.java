package gdg.hongik.project.gollazoom.presets.controller;

import gdg.hongik.project.gollazoom.presets.dto.response.PresetDetailResponse;
import gdg.hongik.project.gollazoom.presets.service.PresetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/presets")
public class PresetController {
    private final PresetService presetService;

    @GetMapping("/{presetId}")
    public ResponseEntity<PresetDetailResponse> getPresetDetail(@PathVariable Long presetId) {
        Long userId = (Long) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        return ResponseEntity.ok(presetService.getPresetDetail(userId, presetId));
    }
}
