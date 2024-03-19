package jpabook.jpashop.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import org.aspectj.weaver.ast.Or;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

@Entity
@Table(name = "orders")
@Getter @Setter
public class Order {
	@Id @GeneratedValue
	@Column(name = "order_id")
	private  Long id;

	//여기를 보면 지연로딩 상태인데 이것의 의미는 디비에서 긁어오지 않는다는 것을 의미
	@ManyToOne(fetch= FetchType.LAZY)
	@JoinColumn(name="member_id") // set fk name member_id
	private Member member; //그래도 뭔가를 넣어는 둬야하니(null) 을 넣은채로 둘 수는 없으니까
	//hibernate 가 new ProxyMember() 객체 라는 걸 만들어서 넣어둡니다. 그게 바로 bytebuddy 입니다.
	//즉 Member 안에 ByteBuddyInterceptor 가 new 된채로 있는 상태입니다.
	//프록시를 가짜로 두고 뭔가가 member 객체를 이용해서 가져가려고 할 때 그 때 sql을 날려서 채워준다고 생각하면 됩니다.
	//그것을 프록시를 초기화 한다고 한다.
	//만약 잭슨 객체가 프록시 객체를 반환하려고 하면, 문제가 발생합니다.
	//이렇게 지연 로딩인 경우 json라이브러리가 뿌리지 말하고 할 수가 있는데 그건 hibernate5Module을 설치해야만 합니다.

	@BatchSize(size = 1000)
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
	private List<OrderItem> orderItems = new ArrayList<>();

	//there is cascade spread persist
	@OneToOne(fetch=FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name="delivery_id")
	private Delivery delivery;

	//this is java8 (if you use Date you have to use other annotation to mapping but it was not need
	private LocalDateTime orderDate; //order Time

	@Enumerated(EnumType.STRING)
	private OrderStatus status; //ORDER status, [ORDER, CANCEL]
	
	//연관관계 편의 메서드
	//Association convenience method
	public void setMember(Member member){
		this.member = member;
		member.getOrders().add(this);
	}

	public void addOrderItem(OrderItem orderItem){
		orderItems.add(orderItem);
		orderItem.setOrder(this);

	}

	public void setDelivery(Delivery delivery){
		this.delivery = delivery;
		delivery.setOrder(this);
	}

	//Create Method
	//생성 메서드
	public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems){
		Order order = new Order();
		order.setMember(member);
		order.setDelivery(delivery);
		for(OrderItem orderItem : orderItems){
			order.addOrderItem(orderItem);
		}
		order.setStatus(OrderStatus.ORDER);
		order.setOrderDate(LocalDateTime.now());
		return order;
	}

	// business logic
	/**
	 * 주문 취소
	 */
	public void cancel(){
		if (delivery.getStatus() == DeliveryStatus.COMP){
			throw new IllegalStateException("이미 배송 완료된 상품은 취소가 불가능합니다.");
		}
		this.setStatus(OrderStatus.CANCEL);
		for (OrderItem orderItem : orderItems) {
			orderItem.cancel();
		}
	}
	//조회 로직
	/**
	 * 전체 주문 가격 조회
	 */
	public int getTotalPrice(){
		// int totalPrice = 0;
		// for (OrderItem orderItem : orderItems) {
		// 	totalPrice += orderItem.getTotalPrice();
		// }
		// return totalPrice;
		return orderItems.stream().mapToInt(OrderItem::getTotalPrice).sum();
	}
}
