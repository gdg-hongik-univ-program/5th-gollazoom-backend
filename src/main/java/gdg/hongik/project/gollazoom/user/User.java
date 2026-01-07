package gdg.hongik.project.gollazoom.user;

import gdg.hongik.project.gollazoom.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 임시로 대충 만들었어요.. 닉네임만 받는 형식으로.. 그니까 무시해도 됩니다.
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// 가입일, 수정일 같은 로그/관리 기능을 위해 BaseEntity 상속
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String nickname;

    public User(String nickname) {
        this.nickname = nickname;
    }
}
