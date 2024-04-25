package dev.project.service;

import dev.project.dto.MemberDto;
import dev.project.entity.Member;
import dev.project.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Autowired
    private MailService emailService;

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


}
