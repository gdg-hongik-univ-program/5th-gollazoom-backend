package gdg.hongik.project.gollazoom.global.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass // 자식 칼럼 중 공통된 칼럼을 부모 클래스로 모으고 객체 상속 관계로 만든다.
@EntityListeners(AuditingEntityListener.class) // 메타데이터 자동 구성
public class BaseEntity {

    @CreatedDate
    @Column(updatable = false, nullable = false) // 한 번 기록되면 수정될 수 없다. null을 허용하지 않겠다.
    protected LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false) // 수정될 수 있으므로 updatable은 기본값인 True.
    protected LocalDateTime updatedAt;
}
