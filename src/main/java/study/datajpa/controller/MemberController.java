package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

import javax.annotation.PostConstruct;

@RestController
@RequiredArgsConstructor
public class MemberController {

    @Autowired
    private MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUserName();
    }

    //domain class converter
    //http 요청은 회원 'id'를 받지만 도메인 클래스 컨버터가 중간에 동작
    //회원 엔티티 객체를 반환
    //도메인 클래스 컨버터도 리파지토리를 사용해서 엔티티를 찾음
    //도메인 클래스 컨버터로 엔티티를 파라미터로 받으면, 이 엔티티는 단순 조회용으로만 사용!!
    //데이터를 변경하지 말자
    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member) {
        return member.getUserName();
    }

    @GetMapping("/members")
    public Page<MemberDto> list(@PageableDefault(size = 5, sort = "userName"
            , direction = Sort.Direction.DESC ) Pageable pageable) {
        return memberRepository.findAll(pageable).map(MemberDto::new);
    }

    //init 메소드는 WAS가 띄워질 때 실행된다.
    //객체가 생성된 후 별도의 초기화 작업을 위해 실행하는 메소드
    //http://localhost:8080/members?page=0&size=3&sort=id,desc&sort=userName,desc
    @PostConstruct
    public void init() {
        for(int i=0 ; i<100 ; i++){
            memberRepository.save(new Member("user" + i, i));
        }

    }
}
