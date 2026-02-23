package gdg.hongik.project.gollazoom.global.api;

// 예외를 상태코드로 바꿉니다.

import gdg.hongik.project.gollazoom.wears.exception.WearForbiddenException;
import gdg.hongik.project.gollazoom.wears.exception.WearNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> badRequest(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(e.getMessage(), null));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> conflict(IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.fail(e.getMessage(), null));
    }

    @ExceptionHandler(WearNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> notFound(WearNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.fail(e.getMessage(), null));
    }

    @ExceptionHandler(WearForbiddenException.class)
    public ResponseEntity<ApiResponse<Void>> handleForbidden(WearForbiddenException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.fail(e.getMessage(), null));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        String msg = "아이디 형식이 맞지 않습니다.";

        String raw = String.valueOf(e.getMostSpecificCause().getMessage());
        if (raw.contains("users") && raw.contains("username")) {
            msg = "이미 사용 중인 아이디입니다.";
        }

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.fail(msg, null));
    }


}
