package jpabook.jpashop.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

	//이렇게 엔티티를 날리면 Json ignore로 살짝 해결 할수는 있다. 다만 엔티티 안에서 이렇게 하면 안됩니다.
	//이렇게 되면 엔티티에 화면을 위한 presentation 계층을 위한 로직이 엔티티에 추가 되는 문제가 있다.
	//api를 위한 기능이 엔티티에 들어오기 시작하면 엔티티로 의존관계가 들어와야만 하는데 오히려 엔티리에서 의존관계가 나간 것이 문제이다.
	//이렇게 되면 양방향으로 의존관계가 걸리면서, 어플리케이션 수정이 어려워진다.
	//또한 양방향 연관관계가 있으면 한쪽은 반드시 Json ignore을 걸어줘야만 한다.
	@JsonIgnore // spring은 기본적으로 jackson을 사용한다. 이렇게 하면 엔티티를 리턴할 때 회원정보는 빠진다.
	@OneToMany(mappedBy = "member") // mean I am just mirror , so if any input in here nothing change with fk
	private List<Order> orders = new ArrayList<>();
}
