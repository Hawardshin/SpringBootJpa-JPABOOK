package jpabook.jpashop;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class MemberRepository {
	@PersistenceContext
	private EntityManager em;

	//멤버 반환 안 하는 이유 : command와 쿼리를 분리하는 김영한 스타일
	public Long save(Member member){
		em.persist(member);
		return member.getId();
	}

	public Member find(Long id){
		return em.find(Member.class,id);
	}
}
