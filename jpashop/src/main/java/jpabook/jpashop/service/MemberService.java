package jpabook.jpashop.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;


	/**
	 * 회원 가입
	 * (join)
	 */
	//default readonly false
	@Transactional
	public Long join(Member member){
		validateDuplicateMember(member);// validate duplicate user
		memberRepository.save(member);
		return member.getId();
	}

	private void validateDuplicateMember(Member member) {
		//EXCEPTION
		List<Member> findMembers = memberRepository.findByName(member.getName());
		if (!findMembers.isEmpty()){
			throw new IllegalStateException("이미 존재하는 회원입니다.");
		}
	}

	//look up all joined user

	public List<Member> findMembers(){
		return memberRepository.findAll();
	}


	public Member findOne(Long memberId){
		//원래 getOr 같은 걸 써야하지만 일단 get으로 하겠습니다.
		return memberRepository.findById(memberId).get();
	}

	@Transactional
	public void update(Long id, String name) { // update할 때 그냥 member를 반환해도 되는데, 사실 영속상태가 끊긴 멤버가 반환이 되기 때문에 써도 되는데...!
		//이렇게 하면 결국 update를 하면서 쿼리를 하는 꼴이 되기 때문에...! 커맨드와 쿼리를 분리한다는 원칙을 위반합니다.(영한 선생님 개인적 원칙)
		//즉 멤버를 리턴하게 되면 커맨드와 쿼리를 같이 보내는 꼴이라서 좋지 않고 그냥 id 정도만 반환하거나 반환 하지 않습니다.
		Member member = memberRepository.findById(id).get(); //멤버가 영속 상태 입니다. db에서 영속성 컨텍스트 올린 것을 반환해준다.
		member.setName(name);//영속 상태 멤버를 setName으로 이름을 바꿔주게 되면 이게 종료가 되면 spring의 AOP가 동작하면서, 트랜젝션 어노테이션 효과로 인해서 트랜젝션이 끝나면서 커밋이 됩니다.
		//그때 jpa가 flush하고 영속성 컨텍스트를 commit 해버리는 것 입니다.
		//즉 영속성 컨텍스트 커밋하고 데이터 베이스를 커밋하는 것 입니다.
	}
}
