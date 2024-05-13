package dev.project.userservice.repository;

import dev.project.userservice.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    // 이메일을 기준으로 회원 정보를 조회합니다.
    Member findByEmail (String email);

}
