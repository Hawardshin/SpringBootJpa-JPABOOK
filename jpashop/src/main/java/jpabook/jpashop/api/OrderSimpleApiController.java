package jpabook.jpashop.api;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * xToOne
 * Order
 * Order -> Member
 * Order -> Delivery
 * 이번시간은 n to one 관계에 대해서 먼저 알아봅니다.
 * x to many 는 컬렉션의 개념이 들어가서 어렵기 때문입니다.
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {
    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/simple- orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName(); //  이렇게 하면 getMember 까지는 proxy 멤버임. 이렇게 되면 Lazy가 강제 초기화 된다.
            order.getDelivery().getAddress();
            //
        }
        return all;
        //이 코드를 그대로 사용하면 양방향 연관관계 문제가 발생한다.
    }
}
