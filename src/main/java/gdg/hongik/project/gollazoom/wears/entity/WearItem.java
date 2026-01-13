package gdg.hongik.project.gollazoom.wears.entity;

import gdg.hongik.project.gollazoom.closet.entity.Cloth;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "wear_items")
public class WearItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "wear_id", nullable = false)
    private Wear wear;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cloth_id", nullable = false)
    private Cloth cloth;

    private WearItem(Cloth cloth) {
        this.cloth = cloth;
    }

    public static WearItem of(Cloth cloth) {
        return new WearItem(cloth);
    }

    void setWear(Wear wear) {
        this.wear = wear;
    }
}
