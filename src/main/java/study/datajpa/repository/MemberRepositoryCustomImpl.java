package study.datajpa.repository;

import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

//규칙: 리포지토리 인터페이스 이름 + Impl
//스프링 데이터 JPA가 인식해서 스프링 빈으로 등록
@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {

    @PersistenceContext
    private final EntityManager em;

    //순수한 jpa 사용
    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }
}
