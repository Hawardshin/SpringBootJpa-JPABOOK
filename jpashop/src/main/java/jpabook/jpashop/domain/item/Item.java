package jpabook.jpashop.domain.item;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jpabook.jpashop.domain.Category;
import jpabook.jpashop.execption.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

@BatchSize(size= 100)
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter
@Setter
public abstract class Item {
	@Id
	@GeneratedValue
	@Column(name = "item_id")
	private Long id;

	private String name;
	private int price;
	private int stockQuantity;

	@ManyToMany(mappedBy = "items")
	private List<Category> categories = new ArrayList<>();

	//--- business logic ---- //

	/**
	 * stock increase
	 */
	public void addStock(int quantity){
		this.stockQuantity += quantity;
	}

	/**
	 * decrease stock
	 */
	public void removeStock(int quantity){
		int restStock = this.stockQuantity - quantity;
		if (restStock < 0){
			throw new NotEnoughStockException("need more stock");
		}
		this.stockQuantity = restStock;
	}
}
