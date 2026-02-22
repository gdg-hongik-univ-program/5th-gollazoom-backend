package gdg.hongik.project.gollazoom.wears.exception;

/** exception 패키지와 커스텀 예외를 만드는 이유는
 *  명확한 HTTP 상태 코드를 반환하기 위함.
 *  단순 IllegalArgumentException으로 뺄 수 없다.
 *  위 예외는 입력값에 문제가 생겼을 때는 사용할 수 있지만,
 *  리소스, 권한, 충돌과 관련된 내용은 커스텀 예외로 HTTP 상태 코드 반환하는 것이 맞다.
 */
public class WearNotFoundException extends RuntimeException {
    public WearNotFoundException(String message) {
        super(message);
    }
}
