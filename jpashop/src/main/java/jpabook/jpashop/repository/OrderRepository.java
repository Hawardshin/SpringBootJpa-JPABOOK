package jpabook.jpashop.repository;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderRepository {
	private final EntityManager em;

	public void save(Order order){
		em.persist(order);
	}

	public Order findOne(Long id) {
		return em.find(Order.class, id);
	}

	public List<Order> findAllByString(OrderSearch orderSearch){
		String jpql = "select o from Order o join o.member m";
		boolean isFirstCondition = true;
		
		//주문 상태 검색
		if (orderSearch.getOrderStatus() != null) {
			if (isFirstCondition) {
				jpql += " where";
				isFirstCondition = false;
			}else {
				jpql += " and";
			}
			jpql += "o.status = :status";
		}

		//회원 이름 검색
		if (StringUtils.hasText(orderSearch.getMemberName())) {
			if (isFirstCondition) {
				jpql += " where";
				isFirstCondition = false;
			} else {
				jpql += " and";
			}
			jpql += " m.name like :name";
		}

		TypedQuery<Order> query = em.createQuery(jpql, Order.class)
			.setMaxResults(1000); //최대 1000건
		if (orderSearch.getOrderStatus() != null) {
			query = query.setParameter("status", orderSearch.getOrderStatus());
		}
		if (StringUtils.hasText(orderSearch.getMemberName())) {
			query = query.setParameter("name", orderSearch.getMemberName());
		}
		return query.getResultList();
	}

	/**
	 * JPA Criteria
	 */
	public List<Order> findAllByCriteria(OrderSearch orderSearch){
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Order> cq = cb.createQuery(Order.class);
		Root<Order> o = cq.from(Order.class);
		Join<Order, Member> m = o.join("member", JoinType.INNER); //회원과 조인
		List<Predicate> criteria = new ArrayList<>();
		//주문 상태 검색
		if (orderSearch.getOrderStatus() != null) {
			Predicate status = cb.equal(o.get("status"),
				orderSearch.getOrderStatus());
			criteria.add(status);
		}
		//회원 이름 검색
		if (StringUtils.hasText(orderSearch.getMemberName())) {
			Predicate name =
				cb.like(m.<String>get("name"), "%" + orderSearch.getMemberName()
					+ "%");
			criteria.add(name);
		}

		cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
		TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000); //최대 1000 건
		return query.getResultList();
	}

	// 재활용 가능
    public List<Order> findAllWithMemberDelivery() {// 이경우는 lazy 로딩이 아니라 한번에 값을 다 가져와서 join을 미리 시켜버립니다.
		//이러한 방법을 fetch join 이라고 한다. --> 매우 중요(꼭 책이나 강좌를 통해 이해하기) -> 대부분의 성능문제는 여기서 해결된다.
		//jpa만 있는 fetch라는 문법
		return em.createQuery(
				"select o from Order o" +
						" join fetch o.member m" +
						" join fetch o.delivery", Order.class
		).getResultList();
    }

	//화면에는 최적화 단, 재사용 불가라는 단점이 존재.
	//또한 엔티티를 바꾸는게 아니라 dto를 조회하는 것
	//단 화면 로직에 사실상 의존하고 있어서 계층이 붕괴 딜 수 있다는 단점이 존재한다.
	// 그리고 이것이 성능을 그렇게 크게 늘려주는 것도 아닐 수 있다.
	public List<OrderSimpleQueryDto> findOrderDtos() {
//		이렇게는 안된다는 걸 알고 가기! 꼭 new 를 써야만 합니다.
//		return em.createQuery(
//				"select o from Order o" +
//						" join o.member m" +
//						" join o.delivery d", OrderSimpleQueryDto.class
//		).getResultList();
		return em.createQuery(
				"select new jpabook.jpashop.repository.OrderSimpleQueryDto(o.id,  m.name, o.orderDate,o.status, d.address)" +
						"from Order o" +
						" join o.member m" +
						" join o.delivery d", OrderSimpleQueryDto.class
		).getResultList();
	}
}
