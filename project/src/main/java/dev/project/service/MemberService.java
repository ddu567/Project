package dev.project.service;

import dev.project.entity.Member;
import dev.project.dto.UpdateMemberDto;
import dev.project.dto.MemberDto;
import dev.project.repository.MemberRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Member registerNewMember(MemberDto memberDto) {
        Member member = new Member();
        member.setEmail(memberDto.getEmail());
        member.setPassword(passwordEncoder.encode(memberDto.getPassword())); // 비밀번호 암호화
        member.setName(memberDto.getName());
        member.setPhone((memberDto.getPhone()));
        member.setAddress(memberDto.getAddress());

        memberRepository.save(member);

        return member;
    }

    @Transactional
    public Member updateMember(String email, UpdateMemberDto updateMemberDto) {
        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            throw new RuntimeException("Member not found with email: " + email);
        }

        member.setPhone(updateMemberDto.getPhone());
        member.setAddress(updateMemberDto.getAddress());

        return memberRepository.save(member);
    }

    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }

}
