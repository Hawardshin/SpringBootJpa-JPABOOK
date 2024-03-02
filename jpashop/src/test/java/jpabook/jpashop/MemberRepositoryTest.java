package jpabook.jpashop;

import static org.junit.Assert.*;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MemberRepositoryTest {
	@Autowired
	MemberRepository memberRepository;

	@Test
	@Transactional
	@Rollback(false)
	public void testMember() throws  Exception{
		//given
		Member member = new Member();
		member.setUsername("memberA");
		
		//when
		Long saveId = memberRepository.save(member);
		Member findMember = memberRepository.find(saveId);

		//then
		Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
		Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
		Assertions.assertThat(findMember).isEqualTo(member);
		//같은 트랜젝션 안에서는 영속성 컨텍스트가 동일하므로 둘은 같다는 것을 알 수가 있다.
		System.out.println("findMember, member"+ (member  ==findMember));
	}
}