package jpabook.jpashop.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {
	private final OrderRepository orderRepository;
	private final MemberRepository memberRepository;
	private final ItemRepository itemRepository;
	//주문
	@Transactional
	public Long order(Long memberId, Long itemId, int count){

		//엔티티 조회
		Member member = memberRepository.findById(memberId).get();
		Item item = itemRepository.findOne(itemId);

		//배송정보 생성
		Delivery delivery = new Delivery();
		delivery.setAddress(member.getAddress());
		
		//주문상품 생성
		OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);
		
		//주문 생성
		Order order = Order.createOrder(member, delivery, orderItem); //여기서 여러개의 상품을 넘길 수도 있습니다.
		
		//주문 저장
		orderRepository.save(order);
		return order.getId();
	}
	//주문 취소
	@Transactional
	public void cancelOrder(Long orderId){
		//주문 엔터티 조회
		Order order = orderRepository.findOne(orderId);
		//주문 취소
		order.cancel();
		//이렇게 바뀌면 알아서 변경된 값들을 db에 업데이트 쿼리가 날라갑니다. (jpa이기 때문에 가능)
	}
	//검색
	public List<Order> findOrders(OrderSearch orderSearch){
		return orderRepository.findALll(orderSearch);
	}

}
