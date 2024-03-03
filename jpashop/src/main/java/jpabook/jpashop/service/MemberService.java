package jpabook.jpashop.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.AllArgsConstructor;
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
		return memberRepository.findOne(memberId);
	}
}
