package study.datajpa.entity;


import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public class BaseEntity extends BaseTimeEntity {

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    //createdBY, lastModifiedBy는
    //AuditorAware<String>를 @Bean에 등록하고 호출될 때 거기서 return 해주는 값을 채워 넣는다
    //실제로 세션에서 id를 가져와서 넣는 방법도 이와 같은 방법
//    @CreatedBy
//    @Column(updatable = false)
//    private String createdBy;
//
//    @LastModifiedBy
//    private String lastModifiedBy;
}
