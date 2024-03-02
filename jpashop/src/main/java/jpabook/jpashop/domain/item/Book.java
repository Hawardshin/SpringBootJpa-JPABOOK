package jpabook.jpashop.domain.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("B")// if default is class Name. so now default = Book
@Getter @Setter
public class Book extends Item{

	private String author;
	private String isbn;

}
