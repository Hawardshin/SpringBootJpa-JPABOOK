package jpabook.jpashop.api;

import jakarta.validation.Valid;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;

    @PostMapping("/api/v1/members")

    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member){ //valid 어노테이션을 사용하면 Member에 있는 @NotEmpty, @NotNull 등의 어노테이션을 사용할 수 있다? 몰라 보기
    // @RequestBody @Valid Member member -> json데이터를 멤버로 바꿔주는 것 (json 말고 다른 것도 되긴 함.)
        //이렇게 보내면 NULL이라도 들어갈 수 있다.
        //엔티티에 Not Empty붙히는 방식 사용 가능
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request){
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @Data
    static class CreateMemberRequest{
        private String name;
    }

    @Data
    static class CreateMemberResponse{
        private Long id;

        public CreateMemberResponse(Long id){
            this.id = id;
        }

    }
}
