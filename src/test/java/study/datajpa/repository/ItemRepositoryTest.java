package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.datajpa.entity.Item;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    /**
     * 식별자가 객체일 때 null 로 판단
     * 식별자가 자바 기본 타입일 때 0 으로 판단
     * Persistable 인터페이스를 구현해서 판단 로직 변경 가능
     * 실제 persist 호출 되면 id가 채워짐
     */
    @Test
    public void save() {
        /**
         * JPA 식별자 생성 전략이 @Id 만 사용해서 직접 할당이면 이미 식별자 값이 있는 상태로 save() 를 호출
         * 이 경우 merge() 가 호출된다. merge() 는 우선 DB를 호 출해서 값을 확인하고(select query실행)
         * DB에 값이 없으면 새로운 엔티티로 인지하므로 매우 비효율적(save에 대한 기능을 제공함) - merge를 사용하는 상황은 거의 없고 쓰지 않도록 해야함
         * Persistable 를 사용해새로운 엔티티 확인 여부를 직접 구현하게는 효과적
         * 참고로 등록시간( @CreatedDate )을 조합해서 사용하면 이 필드로 새로운 엔티티 여부를 편리하게 확인 가능 (@CreatedDate에 값이 없으면 새로운 엔티티로 판단)
         */

        Item item = new Item("A");
        itemRepository.save(item);
    }

}