package gdg.hongik.project.gollazoom.closet.controller;

import gdg.hongik.project.gollazoom.closet.dto.ClosetCreateRequest;
import gdg.hongik.project.gollazoom.closet.dto.ClosetItemListResponse;
import gdg.hongik.project.gollazoom.closet.dto.ClosetResponse;
import gdg.hongik.project.gollazoom.closet.dto.ClosetUpdateRequest;
import gdg.hongik.project.gollazoom.closet.service.ClosetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Bean으로 등록하고 자바 객체를 HTTP 응답 본문에 직접 넣는다.
@RequiredArgsConstructor
@RequestMapping("/api/closet")
public class ClosetController {
    private final ClosetService closetService; // 아직 서비스 계층이 구현되지 않음.

    /*
        미션 코스때와는 달리 user만의 소유의 개념이 존재하므로 코딩의 방향이 달라진다.
        RequestParam -> RequestHeader, RequestBody
        요청의 메타 정보와 비즈니스 데이터를 분리하기 위해
        인증/식별 정보는 Header로, 도메인 데이터는 Body로 받도록 설계한다.
        ResponseEntity(X) -> ResponseStatus
    */

    /**
     * 옷을 등록합니다. required = true 추후 수정 예정.
     * @param auth
     * @param request
     * @return
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClosetResponse create(
            Authentication auth,
            @RequestBody ClosetCreateRequest request
    ) {
        Long userId = (Long) auth.getPrincipal();
        return closetService.create(userId, request);
    }

    /**
     * 옷장에 등록된 모든 옷을 조회합니다.
     * @param auth
     * @return
     */
    @GetMapping
    public List<ClosetItemListResponse> clothingList(
            // required = false 위와 같은 이유.
            Authentication auth
    ) {
        Long userId= (Long) auth.getPrincipal();
        return closetService.list(userId);
    }

    /**
     * 옷장에 등록된 옷 중 하나의 옷만 상세조회 합니다.
     * @param auth
     * @param clothId
     * @return
     */
    @GetMapping("/{clothId}")
    public ClosetResponse get(
            // required = false 위와 같은 이유.
            Authentication auth,
            @PathVariable Long clothId // clothId를 받아와야 한다.
    ) {
        Long  userId= (Long) auth.getPrincipal();
        return closetService.get(userId, clothId);
    }

    /**
     * 옷의 정보를 수정합니다.
     * 수정할 수 있는 변수는 카테고리, 계절, 색깔, 메모, 이미지 입니다.
     * @param auth
     * @param clothId
     * @param request
     * @return
     */
    @PatchMapping("/{clothId}")
    public ClosetResponse update(
            // required = false 위와 같은 이유.
            Authentication auth,
            @PathVariable Long clothId,
            // 업데이트 내용이 Body에 있으니까 여기는 @RequestBody가 필요하다.
            @RequestBody ClosetUpdateRequest request
    ) {
        Long userId= (Long) auth.getPrincipal();
        return closetService.update(userId, clothId, request);
    }

    /**
     * 지정된 옷을 삭제합니다.
     * @param auth
     * @param clothId
     */
    @DeleteMapping("/{clothId}")
    // Delete는 돌려줄 데이터가 없으므로 ResponseStatus.
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            // required = false 위와 같은 이유.
            Authentication auth,
            @PathVariable Long clothId
    ) {
        Long  userId= (Long) auth.getPrincipal();
        closetService.delete(userId, clothId);
    }
}