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


    @Builder
    private User(String username, String password, String nickname) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
    }
}
