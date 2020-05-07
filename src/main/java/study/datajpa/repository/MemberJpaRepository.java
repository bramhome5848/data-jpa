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
}
