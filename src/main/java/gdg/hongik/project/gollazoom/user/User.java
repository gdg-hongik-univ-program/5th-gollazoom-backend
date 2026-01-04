package gdg.hongik.project.gollazoom.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 임시로 대충 만들었어요.. 닉네임만 받는 형식으로.. 그니까 무시해도 됩니다.
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String nickname;

    public User(String nickname) {
        this.nickname = nickname;
    }
}
