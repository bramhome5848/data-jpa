package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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

    @PersistenceContext
    EntityManager em;


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

    @Test
    public void paging() {

        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        //page 0부터 시작
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "userName"));
        int age = 10;

        //when
        //페이징 + total
        //반환 타입에 따라 total count를 날릴지 말지 결정됨
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        //dto 변환
        Page<MemberDto> map = page.map( m -> new MemberDto(m.getId(), m.getUserName(), null));

        //then
        //slice는 3개 요청하면 +1 해서 4개를 요청해봄
        List<Member> content1 = page.getContent();
        long totalElements = page.getTotalElements();

        assertThat(content1.size()).isEqualTo(3);
        assertThat(totalElements).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);   //페이지 번호
        assertThat(page.getTotalPages()).isEqualTo(2);  //전체 페이지수
        assertThat(page.isFirst()).isTrue();    //첫번재 페이지냐??
        assertThat(page.hasNext()).isTrue();    //다음 페이지가 있냐??
    }

    @Test
    public void bulkUpdate() throws Exception {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        //when
        // 벌크 업데이트도 쿼리 종류이므로 이전 영속성에 쌓여있는 쿼리는 실행된 이후 해당 쿼리 실행
        // 벌크 연산은 영속성 컨텍스트를 무시하고 실행
        // 영속성 컨텍스트에 있는 엔티티의 상태와 DB에 엔티티 상태가 달라질 수 있다.
        int resultCount = memberRepository.bulkAgePlus(20);

        //영속성 컨텍스트와 디비 간의 데이터 일치를 위해서
        //em.clear() -> @Modifying(clearAutomatically = false)
        List<Member> result = memberRepository.findByUserName("member5");
        Member member5 = result.get(0);
        System.out.println("member5 = " + member5);


        //then -> 업데이트 된 데이터 수 리턴됨
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void findMemberLazy() {
        //given
        //member1 -> teamA
        //member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);

        memberRepository.save(member1);
        memberRepository.save(member2); //saveAndFlush()로 변경 가능

        em.flush();  //Spring data Jpa -> Repository.flush() 있음
        em.clear();  //

        //when
        //select Member
        //List<Member> members = memberRepository.findAll();
        //List<Member> members = memberRepository.findMemberFetchJoin();
        //List<Member> members = memberRepository.findEntityGraphByUserName("member1");
        List<Member> members = memberRepository.findEntityGraph2ByUserName("member1");

        for (Member member : members) {
            System.out.println("member = " + member.getUserName());
            System.out.println("member.teamClass = " + member.getTeam().getClass());
            System.out.println("member.team = " + member.getTeam().getName());
        }
    }

    @Test
    public void queryHint() {
        //givn
        Member member1 = memberRepository.save(new Member("member1", 10));
        memberRepository.save(member1);
        em.flush();
        em.clear();

        //when
        // -> 일반 조회의 경우 초기 데이터의 스냅샷과 변경될 객체에 대한 메모리를 둘 다 사용하게 됨
        // -> 최적화를 위해 조회만 할 경우 -> jpa hint 사용
        Member findMember = memberRepository.findReadOnlyByUserName("member1");
        findMember.setUserName("member2");  // read only -> 스냅샷을 만들지 않음 -> 변경해도 업데이트 되지 않음

        em.flush();
    }

    @Test
    public void lock() {

        //givn
        Member member1 = memberRepository.save(new Member("member1", 10));
        memberRepository.save(member1);
        em.flush();
        em.clear();

        //when
        List<Member> result = memberRepository.findLockByUserName("member1");
        //for update
    }

    //사용자가 정의한 repository 구현
    @Test
    public void callCustom() {
        List<Member> result = memberRepository.findMemberCustom();
    }

    /**
     * @Repository 적용: JPA 예외를 스프링이 추상화한 예외로 변환
     * @Transactional 트랜잭션 적용
     * JPA의 모든 변경은 트랜잭션 안에서 동작
     * 스프링 데이터 JPA는 변경(등록, 수정, 삭제) 메서드를 트랜잭션 처리
     * 서비스 계층에서 트랜잭션을 시작하지 않으면 리파지토리에서 트랜잭션 시작
     * 서비스 계층에서 트랜잭션을 시작하면 리파지토리는 해당 트랜잭션을 전파 받아서 사용
     * 그래서 스프링 데이터 JPA를 사용할 때 트랜잭션이 없어도 데이터 등록, 변경이 가능했음(사실은 트랜 잭션이 리포지토리 계층에 걸려있는 것임)
     * 데이터를 단순히 조회만 하고 변경하지 않는 트랜잭션에서 readOnly = true 옵션을 사용하면 플러 시를 생략해서 약간의 성능 향상을 얻을 수 있음
     */

}