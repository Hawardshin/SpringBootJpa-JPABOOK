package jpabook.jpashop.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter @Setter
public class Order {
	@Id @GeneratedValue
	@Column(name = "order_id")
	private  Long id;

	@ManyToOne(fetch= FetchType.EAGER)
	@JoinColumn(name="member_id") // set fk name member_id
	private Member member;

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
}
