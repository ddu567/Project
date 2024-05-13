package dev.project.userservice.service;

import dev.project.userservice.dto.MemberDto;
import dev.project.userservice.dto.UpdateMemberDto;
import dev.project.userservice.entity.Member;
import dev.project.userservice.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

// 회원 관련 서비스를 제공하는 클래스
@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository; // 회원 정보를 관리하는 레포지토리

    @Autowired
    private PasswordEncoder passwordEncoder; // 비밀번호 암호화를 위한 인코더

    // 새 회원 등록
    public Member registerNewMember(MemberDto memberDto) {
        Member member = new Member();
        member.setEmail(memberDto.getEmail());
        member.setPassword(passwordEncoder.encode(memberDto.getPassword())); // 비밀번호 암호화
        member.setName(memberDto.getName());
        member.setPhone(memberDto.getPhone());
        member.setAddress(memberDto.getAddress());
        memberRepository.save(member); // DB에 회원 정보 저장
        return member;
    }

    // 회원 정보 업데이트
    @Transactional
    public Member updateMember(String email, UpdateMemberDto updateMemberDto) {
        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            throw new RuntimeException("Member not found with email: " + email);
        }
        member.setPhone(updateMemberDto.getPhone());
        member.setAddress(updateMemberDto.getAddress());
        return memberRepository.save(member); // 변경된 정보 저장
    }

    // ID로 회원 조회
    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }

}
