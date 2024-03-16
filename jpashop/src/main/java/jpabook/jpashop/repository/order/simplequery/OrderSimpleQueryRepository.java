package jpabook.jpashop.repository.order.simplequery;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {
    private final EntityManager em;
    //화면에는 최적화 단, 재사용 불가라는 단점이 존재.
    //또한 엔티티를 바꾸는게 아니라 dto를 조회하는 것
    //단 화면 로직에 사실상 의존하고 있어서 계층이 붕괴 딜 수 있다는 단점이 존재한다.
    // 그리고 이것이 성능을 그렇게 크게 늘려주는 것도 아닐 수 있다.
    public List<OrderSimpleQueryDto> findOrderDtos() {
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto(o.id,  m.name, o.orderDate,o.status, d.address)" +
                        "from Order o" +
                        " join o.member m" +
                        " join o.delivery d", OrderSimpleQueryDto.class
        ).getResultList();
    }
}
