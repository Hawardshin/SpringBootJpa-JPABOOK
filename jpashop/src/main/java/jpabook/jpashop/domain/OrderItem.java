package jpabook.jpashop.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class OrderItem {
	@Id @GeneratedValue
	@Column(name = "order_item_id")
	private Long id;

	@ManyToOne
	@JoinColumn(name="item_id")
	private Item item;

	@ManyToOne
	@JoinColumn(name="order_id") //The @JoinColumn annotation specifies the column used to implement this relationship in the actual database table.
	private Order order;

	private Long orderPrice; //order Price because Item cost can change
	private Long count; //order count
}
