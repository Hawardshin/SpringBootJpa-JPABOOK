package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


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

    @GetMapping("/api/v1/simple-orders")
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


    //api 스펙에 맞게 개발 했음.
    //단, 둘 다 LAZY Loading 으로 인해 쿼리가 너무 많이 호출되는 문제가 있다.
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> orderV2(){
        //ORDER 2개가 조회됨
        //N+1  -> 1+ N -> 1+ 회원 N  배송 N
        //N이 2개
        // 첫번째 쿼리의 결과로 n번 만큼 쿼리가 실행되는 것은 n+1문제라고 부릅니다.
        //그렇다고 해결하기 위해서 EAGER를 사용해서는 절대 안된다. -> 필요하면 fetch join을 하시길.
        //지연로딩은 이미 조회된 경우 쿼리 생략함.
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        // 이후 생성자에서
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());
        return result;
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();//LAZY 초기화
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();//LAZY 초기화

        }
    }
}
