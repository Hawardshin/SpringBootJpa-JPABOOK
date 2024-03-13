package jpabook.jpashop.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;

    @GetMapping("/api/v1/members")
    public List<Member> memberV1(){//문제점 : 이렇게 엔티티를 직접 노출하면 원치 않는 데이터도 다 날라갑니다.
        //또한 이전에 말했듯, api 스펙이 바뀌는 문제가 생깁니다.
        return memberService.findMembers();
    }

    @GetMapping("/api/v2/members")
    public Result memberV2(){
        List<Member> findMembers = memberService.findMembers();
        List<MemberDto> collect = findMembers.stream()
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());
        return new Result(collect.size(),collect);
        //이렇게 return 에 dto를 따로 만들면 데이터를 List형식이 아니여도 쉽게 추가를 할 수가 있습니다.
    }

    @Data
    @AllArgsConstructor
    static class Result<T>{ //제네릭을 이용한 return Dto
        private int count;
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto{
        private String name;
    }
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member){ //valid 어노테이션을 사용하면 Member에 있는 @NotEmpty, @NotNull 등의 어노테이션을 사용할 수 있다?
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

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequest request){//업데이트용 REQUEST dto와 업데이트용 응답 dto를 따로 만들었다. 그 이유는 일반적으로 등록과 수정이 범위가 수정이 더 제한적인 경우가 많아서 따로 만드는 경우가 많습니다.
        memberService.update(id, request.getName());//수정 할 때는 변경감지가 좋습니다.
        //update에서 member를 리턴하지 않는 스타일 (쿼리와 명령을 분리한다.)
        //유지 보수성이 많이 증대하기 떄문에 추천합니다.
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor// 롬북을 몇가지 제한해서 사용하는데 엔티티에서는 최대한 사용을 자제하고 ->Getter 정도만 사용 합니다. 단 Dto에는 그냥 막 씁니다. Dto은 데이더만 왔다 갔다 하는 것이기 때문에 많이 씁니다.
    static class UpdateMemberResponse{
        private Long id;
        private String name;
    }

    @Data
    static class CreateMemberRequest{
        @NotEmpty
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
