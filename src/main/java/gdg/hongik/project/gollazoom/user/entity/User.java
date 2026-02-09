package gdg.hongik.project.gollazoom.user.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    private Long id;

    @Column (unique = true, nullable = false)
    private String username;

    @Column
    private String password;

    @Column
    private String nickname;

    @Column
    private LocalTime worktime;

    @Column(nullable = false)
    private boolean isUsingWashUpTech = true; // 기본값으로 세탁 기능 사용을 true로 지정.


    @Builder
    private User(String username, String password, String nickname) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
    }

    public void changeIsUsingWashUpTech(boolean isUsingWashUpTech) {
        this.isUsingWashUpTech = isUsingWashUpTech;
    } // 세탁 기능 사용 변경
}
