package dev.project.repository;

import dev.project.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
//    Member findByEmail (String email);
    Optional<Member> findByEmail(String email);
}
