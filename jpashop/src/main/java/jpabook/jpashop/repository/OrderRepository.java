package jpabook.jpashop.repository;
import java.util.ArrayList;
import java.util.List;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jpabook.jpashop.domain.*;
import org.aspectj.weaver.ast.Or;
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
import lombok.RequiredArgsConstructor;

import static jpabook.jpashop.domain.QMember.*;
import static jpabook.jpashop.domain.QOrder.*;

@Repository
public class OrderRepository {
	private final EntityManager em;
	private final JPAQueryFactory query;

	public OrderRepository(EntityManager em) {
		this.em = em;
		this.query = new JPAQueryFactory(em);
	}

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
	//querydsl을 사용하면 코드의 재사용성이 늘어남.
	public List<Order> findAll2(OrderSearch orderSearch){
		return query
				.select(order)
				.from(order)
				.where(statusEq(orderSearch.getOrderStatus()))
				.fetch();
	}

	//오타가 잡히는 강력한 장점. 컴파일 시점에 오타가 잡힌다.
	public List<Order> findALll(OrderSearch orderSearch){
		//여기서 QOrder를 static import해서 코드를 줄일 수 있다.
	/*	QOrder order = QOrder.order;
		QMember member = QMember.member;*/
		return query.select(order)
			.from(order)
			.join(order.member, member)
//			.where(order.status.eq(orderSearch.getOrderStatus())) 정적 쿼리 방식
			//member.name.like(orderSearch.getName
			.where(statusEq(orderSearch.getOrderStatus()), nameLike(orderSearch.getMemberName()))
			.limit(1000)
			.fetch();
	}

	private BooleanExpression statusEq(OrderStatus statusCond){
		if (statusCond == null) {
			return null;
		}
		return order.status.eq(statusCond);
	}
	private BooleanExpression nameLike(String nameCond){
		if (!StringUtils.hasText(nameCond)) {
			return null;
		}
		return member.name.like(nameCond);
	}

	// 재활용 가능
    public List<Order> findAllWithMemberDelivery() {// 이경우는 lazy 로딩이 아니라 한번에 값을 다 가져와서 join을 미리 시켜버립니다.
		//이러한 방법을 fetch join 이라고 한다. --> 매우 중요(꼭 책이나 강좌를 통해 이해하기) -> 대부분의 성능문제는 여기서 해결된다.
		//jpa만 있는 fetch라는 문법
		//원칙 1: to one 관계는 전부 fetch join을 사용한다.
		return em.createQuery(
				"select o from Order o" +
						" join fetch o.member m" +
						" join fetch o.delivery", Order.class
		).getResultList();
    }


	public List<Order> findAllWithItem() { //db의 distinct말고 한가지 더 기능을 한다.(sql에 distinct) 단 db의 distinct는 한줄이 완전히 똑같해야 distinct가 먹히는데,
		//jpa에서 자체적으로 Order를 가져올 때 같은 값이면 중복을 버려준다.(루트 엔티티의 중복을 걸러서 담아줌)
		// 컬렉션 페치조인을 따로 이야기 하는 이유.
		// 일대다 문제는 distinct 를 통해서 해결하긴 했다.
		// 단, 이 방법은 페이징이 불가능해진다. (일대다를 패치조인 하는 순간 페이징은 아에 불가능)
		//페이징이란 아래 주석 친 것처럼 몇개만 가져오겠다는 의미이다.
		//이게 문제가 페이징이 페치조인 이후에 리미트를 메모리에서 처리하기 때문에 outOfMemory가 날 가능성이 높다.
		//컬렉션 패치 조인은 한개만 사용해라. 데이터가 부정합해질 수 있다.
		return em.createQuery(
				"select distinct o from Order o" +
						" join fetch o.member m" +
						" join fetch o.delivery d" +
						" join fetch o.orderItems oi" +
						" join fetch oi.item i", Order.class)
				//.setFirstResult(1)
				//.setMaxResult(100)
				.getResultList();

	}

	public List<Order> findAllWithMemberDelivery(int offset, int limit) {
		return em.createQuery(
						"select o from Order o" +
								" join fetch o.member m" +
								" join fetch o.delivery", Order.class)
			.setFirstResult(offset)
			.setMaxResults(limit)
			.getResultList();
	}
}
