package jpabook.jpashop.service.query;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class OrderQueryService {

    private final OrderRepository orderRepository;
    public List<OrderDto> OrderV3(){

        List<Order> orders = orderRepository.findAllWithItem();
        List<jpabook.jpashop.service.query.OrderDto> collect = orders.stream()
                .map(o -> new   OrderDto(o))
                .collect(Collectors.toList());
        return collect;
    }
}
