package dev.project.member.repository;

import dev.project.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByEmail (String email);
//    Optional<Member> findByMail (String email);
}
