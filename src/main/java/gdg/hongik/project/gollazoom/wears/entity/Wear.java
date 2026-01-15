package gdg.hongik.project.gollazoom.wears.entity;


import gdg.hongik.project.gollazoom.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// 엔티티와 매핑할 테이블 지정
@Table(
        name = "wears", // wears 테이블과 매핑된다.
        uniqueConstraints = {
                // 한 유저는 특정 날짜에 특정 옷 조합을 하나만 지정할 수 있다.
                @UniqueConstraint(name = "wears_user_date", columnNames = {"user_id", "wear_date"})
        }
)
public class Wear {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "wear_date", nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "wear", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WearItem> items = new ArrayList<>();

    @PrePersist
    void prePersist() {
        // 이게 실행되면 현재 시간을 createAt으로 한다.
        this.createdAt = LocalDateTime.now();
        // 업데이트의 초기값은 createdAt이다.
        this.updatedAt = this.createdAt;
    }

    public void refreshUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }

    public void changeDate(LocalDate date) {
        this.date = date;
    }

    private Wear(User user, LocalDate date) {
        this.user = user;
        this.date = date;
    }

    public static Wear of(User user, LocalDate date) {
        return new Wear(user, date);
    }

    public void addItem(WearItem item) {
        this.items.add(item);
        item.setWear(this);
    }

    /** 새로운 옷을 교체할 경우 사용되는 메서드
     * wear Entity의 본질을 바꾸기 때문에 Service 계층에 없어도 된다.
     *
     * @param newItems : 새롭게 정해진 입을 옷의 리스트
     */
    public void replaceItems(List<WearItem> newItems) {
        this.items.clear();
        for (WearItem item : newItems) {
            this.addItem(item);
        }
    }
}
