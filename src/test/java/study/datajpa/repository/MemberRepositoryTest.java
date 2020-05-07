package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;

    @Autowired TeamRepository teamRepository;

    @Test
    public void testMember() {

        //proxy 클래스 호출됨 -> Interface에 해당하는 구현 클래스를 spring data jpa가 만들어서 injection 해준 것
        System.out.println("memberRepository = " + memberRepository.getClass());

        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        //Optional 받는 것이 맞으나 강제로 주입
        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUserName()).isEqualTo(member.getUserName());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void findByUserNameAndAgeGreaterThan() {

        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByUserNameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUserName()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void findHelloBy() {
        List<Member> helloBy = memberRepository.findTop3HelloBy();
    }

    @Test
    public void testNamedQuery() {

        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUserName("AAA");
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void testQuery() {

        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    public void findUserNameList() {

        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> nameList = memberRepository.findUserNameList();

        for (String s : nameList) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void findMemberDto() {

        Team team1 = new Team("teamA");
        teamRepository.save(team1);

        Member m1 = new Member("AAA", 10);
        m1.setTeam(team1);
        memberRepository.save(m1);

        List<MemberDto> memberDto = memberRepository.findMemberDto();

        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }

    @Test
    public void findByNames() {

        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));

        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void returnType() {

        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> members = memberRepository.findListByUserName("AAA");
        Member member = memberRepository.findMemberByUserName("AAA");
        Optional<Member> oMember = memberRepository.findOptionalByUserName("AAA");

        assertThat(members.get(0)).isEqualTo(member);
        assertThat(members.get(0)).isEqualTo(oMember.get());
        assertThat(member).isEqualTo(oMember.get());

        /**
         * 컬렉션
         * 결과 없음: 빈 컬렉션 반환 단건 조회
         * 결과 없음: null 반환
         * 결과가 2건 이상: javax.persistence.NonUniqueResultException 예외 발생
         * > 참고: 단건으로 지정한 메서드를 호출하면 스프링 데이터 JPA는 내부에서 JPQL의 Query.getSingleResult() 메서드를 호출
         * 이 메서드를 호출했을 때 조회 결과가 없으면 javax.persistence.NoResultException 예외가 발생
         * 스프링 데이터 JPA는 단건을 조회할 때 이 예외가 발생하면 예외를 무시하고 대신에 null 을 반환한다.
         */

        //데이터가 없는 경우 -> null이 아닌 empty collection이 넘어온다
        //List는 절대 null이 넘어오지 않음
        List<Member> result = memberRepository.findListByUserName("asdfsdfsdf");
        System.out.println("result.size() = " + result.size());

        //단건 조회는 없으면 null이 된다
        Member findMember = memberRepository.findMemberByUserName("asdfasdfadfa");
        System.out.println("findMember = " + findMember);

        //따라서 8버전 부터는 Optional로 조회하는 것이 좋음
        Optional<Member> optionalMember = memberRepository.findOptionalByUserName("asdfsdf");
        System.out.println("optionalMember = " + optionalMember);
    }
}