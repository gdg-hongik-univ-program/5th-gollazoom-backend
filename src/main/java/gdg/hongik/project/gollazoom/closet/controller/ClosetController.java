package gdg.hongik.project.gollazoom.closet.controller;

import gdg.hongik.project.gollazoom.closet.dto.ClosetResponse;
import gdg.hongik.project.gollazoom.closet.service.ClosetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController // Bean으로 등록하고 자바 객체를 HTTP 응답 본문에 직접 넣는다.
@RequiredArgsConstructor
@RequestMapping("/api/closet")
public class ClosetController {
    private final ClosetService closetService; // 아직 서비스 계층이 구현되지 않음.

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClosetResponse create(
    )
}