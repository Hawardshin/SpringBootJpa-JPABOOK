package jpabook.jpashop.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Member {
	@Id @GeneratedValue
	@Column(name = "member_id")
	private Long id;

	@NotEmpty // 이걸 해주면 spring boot가 기본으로 400번을 반환하도록 해준다., javax validation
	private String name;

	@Embedded //use built-in type (you can choose one, Embedded or Embeddable but usually use both)
	private Address address;

	@OneToMany(mappedBy = "member") // mean I am just mirror , so if any input in here nothing change with fk
	private List<Order> orders = new ArrayList<>();
}
