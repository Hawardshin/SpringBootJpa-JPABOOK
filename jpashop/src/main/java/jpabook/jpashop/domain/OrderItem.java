package jpabook.jpashop.domain;

import org.aspectj.weaver.ast.Or;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jpabook.jpashop.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

	@Id @GeneratedValue
	@Column(name = "order_item_id")
	private Long id;

	@ManyToOne(fetch= FetchType.LAZY)
	@JoinColumn(name="item_id")
	private Item item;

	@ManyToOne(fetch= FetchType.LAZY)
	@JoinColumn(name="order_id") //The @JoinColumn annotation specifies the column used to implement this relationship in the actual database table.
	private Order order;

	private int orderPrice; //order Price because Item cost can change
	private int count; //order count

	//create Method (생성 메서드)
	public static OrderItem createOrderItem(Item item, int orderPrice, int count){
		OrderItem orderItem = new OrderItem();
		orderItem.setItem(item);
		orderItem.setOrderPrice(orderPrice);
		orderItem.setCount(count);

		item.removeStock(count);
		return orderItem;
	}

	//business logic
	public void cancel(){
		getItem().addStock(count);
	}


	//look up logic
	public int getTotalPrice(){
		return getOrderPrice() * getCount();
	}
}
