package gdg.hongik.project.gollazoom.closet.entity;

import gdg.hongik.project.gollazoom.global.common.BaseEntity;
import gdg.hongik.project.gollazoom.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cloth extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 옷 고유 ID


    // 이 때 FK는 유저의 고유 id가 될 것이다.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false) // 주인 없는 옷 없다.
    private User user;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20) // 카테고리 글자 수는 20자까지
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Season season;

    @Column(length = 30)
    private String color;

    @Column(length = 255)
    private String memo; // 메모는 255자까지

    @Column(nullable = false, length = 500)
    private String imageUrl; // 우선 이미지 url로 했는데, 바뀔수도 있음.

    @Column(nullable = false)
    private boolean isRaining;

    public Cloth(User user, Category category, Season season, String color, String memo, String imageUrl, boolean isRaining) {
        this.user = user;
        this.category = category;
        this.season = season;
        this.color = color;
        this.memo = memo;
        this.imageUrl = imageUrl;
        this.isRaining = isRaining;
    }

    public void update(Category category, Season season, String color, String memo, String imageUrl, boolean isRaining) {
        if (category != null) this.category = category;
        if (season != null) this.season = season;
        if (color != null) this.color = color;
        if (memo != null) this.memo = memo;
        if (imageUrl != null) this.imageUrl = imageUrl;
        this.isRaining = isRaining;
    }
}
