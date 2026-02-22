package gdg.hongik.project.gollazoom.presets.entity;

import gdg.hongik.project.gollazoom.closet.entity.Cloth;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PresetItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch= FetchType.LAZY, optional = false)
    @JoinColumn(name="preset_id",nullable = false)
    private Preset preset;

    @ManyToOne(fetch= FetchType.LAZY, optional = false)
    @JoinColumn(name="cloth_id",nullable = false)
    private Cloth cloth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PresetSlot slot;

    private PresetItem(Cloth cloth, PresetSlot slot) {
        this.cloth = cloth;
        this.slot = slot;
    }

    public static PresetItem of(Cloth cloth, PresetSlot slot) {
        return new PresetItem(cloth, slot);
    }

    void setPreset(Preset preset) {
        this.preset = preset;
    }
}
