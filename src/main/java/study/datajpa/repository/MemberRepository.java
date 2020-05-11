package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {

    //순수 jpa에서 쿼리를 작성 했던 부분을 메소드 이름으로 쿼리 생성
    //단점 -> 함수 명이 너무 길어짐, 파라미터가 2개 정도면 그냥 작성, 그 이상은 쿼리 직접 작성?
    List<Member> findByUserNameAndAgeGreaterThan(String userName, int age);

    //조건을 적지 않으면 전체 조회 -> where 절 없이 전체조회
    //Top3 -> 3개
    List<Member> findTop3HelloBy();

    //NamedQuery
    //함수명으로 도메인.name을 우선 찾고 없으면 메서드 이름으로 쿼리를 생성.
    //namedQuery의 장점 -> 잘못 입력한 쿼리에 대해서 로딩시점에 파싱하며 에러 확인 가능
    //@Query(name = "Member.findByUserName") //선언하지 않아도 동작
    List<Member> findByUserName(@Param("userName") String userName);

    //쿼리 바로 작성 가능 -> 실무에서 많이 사용
    //로딩시점에 에러 확인 가능
    //이름이 없는 namedQuery라고 생각하면 됨
    @Query("select m from Member m where m.userName =:userName and m.age = :age")
    List<Member> findUser(@Param("userName") String userName, @Param("age") int age);

    @Query("select m.userName from Member m")
    List<String> findUserNameList();

    //dto 조회 -> new 사용(전체 경로)
    @Query("select new study.datajpa.dto.MemberDto(m.id, m.userName, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    //파라미터 바인딩 -> 리스트 in절로
    @Query("select m from Member m where m.userName in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);

    //반환 타입
    List<Member> findListByUserName(String userName);   //컬렉션
    Member findMemberByUserName(String userName);   //단건
    Optional<Member> findOptionalByUserName(String userName);   //optioanl

    //페이징
    //추가적인 기능들에 대한 사용 여부는 리턴 타입에 따라 결정됨
    //count쿼리에 대한 별도의 최적화가 필요한 경우 직접 작성
    //기본 쿼리가 복잡해지면 count쿼리 또한 복잡해 질 수 있기 때문에 별도로 작성
    //sort 조건이 복잡한 경우에도 직접 쿼리에서 세팅하는 것이 효율적

    @Query(value = "select m from Member m left join m.team t",
            countQuery = "select count(m.userName) from Member m")
    Page<Member> findByAge(int age, Pageable pageable); //count 쿼리 사용
    //Slice<Member> findByAge(int age, Pageable pageable); //count 쿼리 사용 안 함
    //List<Member> findByAge(int age, Pageable pageable); //count 쿼리 사용 안함 -> 단순 페이징
    //List<Member> findByAge(int age, Sort sort); //단순 sorting

    //bulkupdate
    //@Modifying이 있어야 JPA excuteUpdate()가 실행됨
    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    //fetch join
    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    //공통 메서드 오버라이드
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    //JPQL + 엔티티 그래프
    @EntityGraph(attributePaths = {"team"}) @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    //메서드 이름으로 쿼리에서 특히 편리하다.
    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraphByUserName(@Param("userName") String userName);

    //named EntityGraph 사용
    @EntityGraph("Member.all")
    List<Member> findEntityGraph2ByUserName(@Param("userName") String userName);
}
