package dev.project.userservice.repository;

import dev.project.userservice.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByEmail (String email);
//    Optional<Member> findByMail (String email);
}
