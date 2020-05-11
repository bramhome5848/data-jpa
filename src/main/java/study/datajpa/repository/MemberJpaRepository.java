package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class MemberJpaRepository {

    @PersistenceContext
    private EntityManager em;

    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    public void delete(Member member) {
        em.remove(member);
    }

    public List<Member> findAll() {
        //jpql
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public Optional<Member> findById(Long id) {

        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }

    public long count() {
        return em.createQuery("select count(m) from Member m", Long.class)
                .getSingleResult();
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }

    //순수 jpa의 경우 직접 쿼리 작성
    public List<Member> findByUserNameAndAgeGreaterThan(String userName, int age) {
        return em.createQuery("select m from Member m where m.userName = :userName and m.age > :age", Member.class)
                .setParameter("userName", userName)
                .setParameter("age", age)
                .getResultList();
    }

    //순수 jpa namedQuery
    public List<Member> findByUserName(String userName) {
        return em.createNamedQuery("Member.findByUserName", Member.class)
                .setParameter("userName", userName)
                .getResultList();
    }

    //순수 jpa paing
    public List<Member> findByPage(int age, int offset, int limit) {
        return em.createQuery("select m from Member m where m.age = :age order by m.userName desc", Member.class)
                .setParameter("age", age)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    public long totalCount(int age) {
        return em.createQuery("select count(m) from Member m where m.age = :age", Long.class)
                .setParameter("age", age)
                .getSingleResult();
    }


    // bulk update -> 디비에 직접 쿼리를 날림
    // 벌크 연산은 영속성 컨텍스트를 무시하고 실행하기 때문에, 영속성 컨텍스트에 있는 엔티티의 상태와 DB에 엔티티 상태가 달라질 수 있다.
    /**
     * 권장하는 방안
     * 1. 영속성 컨텍스트에 엔티티가 없는 상태에서 벌크 연산을 먼저 실행한다.
     * 2. 부득이하게 영속성 컨텍스트에 엔티티가 있으면 벌크 연산 직후 영속성 컨텍스트를 초기화 한다.
     */
    public int bulkAgePlus(int age) {
        return em.createQuery(
                "update Member m set m.age = m.age + 1" +
                        " where m.age >= :age")
                .setParameter("age", age)
                .executeUpdate();
    }
}
