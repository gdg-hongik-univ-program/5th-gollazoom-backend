package gdg.hongik.project.gollazoom.presets.entity;

import gdg.hongik.project.gollazoom.closet.entity.Cloth;
import gdg.hongik.project.gollazoom.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class Preset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(nullable=false)
    LocalDateTime createdAt;

    @Column(nullable=false)
    LocalDateTime updatedAt;

    @OneToMany(
            mappedBy = "preset",
            cascade=CascadeType.ALL,
            orphanRemoval = true
    )
    private List<PresetItem> items=new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public void refreshUpdatedAt(){
        this.updatedAt = LocalDateTime.now();
    }

    private Preset(User user, String name) {
        this.user = user;
        this.name = name;
    }

    public static Preset of(User user, String name) {
        return new Preset(user, name);
    }

    public void addItem(PresetItem item) {
        this.items.add(item);
        item.setPreset(this);
    }

    public void replaceItems(List<PresetItem> newItems){
        this.items.clear();
        for (PresetItem item : newItems){
            this.addItem(item);
        }
    }

    public void changeName(String name){
        this.name = name;
    }
}
