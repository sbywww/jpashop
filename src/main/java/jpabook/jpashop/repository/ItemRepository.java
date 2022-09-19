package jpabook.jpashop.repository;

import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item) {
        if (item.getId() == null) {
            em.persist(item);
        } else {
            // 준영속 엔티티의 식별자 값으로 1차 캐시에서 엔티티 조회
            // 만약, 1차캐시에 없다면 데이터베이스에서 엔티티를 조회하고, 1차 캐시에 저장한다.
            // 조회한 영속 엔티티에 준영속 엔티티의 값을 채워 넣는다.
            // 영속 상태인 엔티티를 반환한다.

            // ** 병합시 동작 방식
            // 준영속 엔티티의 식별자 값으로 영속 엔티티를 조회한다.
            // 영속 엔티티의 값을 준영속 엔티티의 값으로 모두 교체한다.(병합한다.)
            // 트랜잭션 커밋 시점에 변경감지기능이 동작해서 데이터베이스에 UPDATE SQL이 실행된다.

            // 주의 : 변경감지 기능을 사용하면 원하는 속성만 선택해서 변경할 수 있지만, 병합을 사용하면 모든 속성이 변경된다.
            // 병합시 값이 없으면 null 로 업데이트 할 위험이 있다.
            em.merge(item);
        }
    }

    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll() {
        return em.createQuery("select i from Item i", Item.class).getResultList();
    }
}
