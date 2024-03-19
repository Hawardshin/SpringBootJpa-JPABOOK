package jpabook.jpashop.repository.order.query;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;

//쿼리쪽은 특정화면에 fit 한 쿼리는 다음과 같이 분리해뒀습니다.
//화면과 관련된 것들은 이쪽에 적어주고
//핵심 로직은 OrderRepository 에 들어갑니다.
//라이프 사이클이 달라서 이렇게 넣곤 합니다.
@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {
    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDtos(){
        List<OrderQueryDto> result = findOrders();
        result.forEach(o->{
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
            o.setOrderItems(orderItems);
        });
        return result;
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        //문제점 이런 경우 컬렉션을 넣을 방법이 없습니다. (jpql의 한계)
        return em.createQuery(
                        "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                                " from OrderItem oi" +
                                " join oi.item i" +
                                " where oi.order.id = :orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    private List<OrderQueryDto> findOrders() {
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)"+
                "from Order o" +
                " join o.member m" +
                " join o.delivery d", OrderQueryDto.class
        ).getResultList();
    }
}
