package dev.project.userservice.service;

import dev.project.userservice.dto.MemberDto;
import dev.project.userservice.dto.UpdateMemberDto;
import dev.project.userservice.entity.Member;
import dev.project.userservice.repository.MemberRepository;
import dev.project.userservice.security.AESUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository; // 회원 정보를 관리하는 레포지토리

    private final PasswordEncoder passwordEncoder; // 비밀번호 암호화를 위한 인코더

    private final RedisTemplate<String, String> redisTemplate; // Redis 템플릿

    private final VerificationService verificationService; // 인증 서비스



    private static final String VERIFICATION_CODE_KEY_PREFIX = "verification-code:";

    // 인증 코드 검증
    private boolean verifyCode(String email, String inputCode) {
        String key = VERIFICATION_CODE_KEY_PREFIX + email;
        String savedCode = redisTemplate.opsForValue().get(key);
        log.debug("저장된 코드: {}, 입력된 코드: {}", savedCode, inputCode);
        return savedCode != null && savedCode.equals(inputCode);
    }

    // 새 회원 등록
    @Transactional
    public boolean registerNewMember(MemberDto memberDto, String inputCode) {
        if (verifyCode(memberDto.getEmail(), inputCode)) {
            Member member = new Member();
            member.setEmail(AESUtil.encrypt(memberDto.getEmail())); // 이메일 암호화
            member.setPassword(passwordEncoder.encode(memberDto.getPassword())); // 비밀번호 암호화
            member.setName(AESUtil.encrypt(memberDto.getName())); // 이름 암호화
            member.setPhone(AESUtil.encrypt(memberDto.getPhone())); // 전화번호 암호화
            member.setAddress(AESUtil.encrypt(memberDto.getAddress())); // 주소 암호화
            memberRepository.save(member);
            redisTemplate.delete(VERIFICATION_CODE_KEY_PREFIX + memberDto.getEmail()); // 인증 코드 삭제
            return true; // 회원 가입 성공
        }
        return false; // 인증 코드 불일치
    }

    // 회원 정보 업데이트
    @Transactional
    public Member updateMember(String email, UpdateMemberDto updateMemberDto) {
        Member member = memberRepository.findByEmail(AESUtil.decrypt(email));
        if (member == null) {
            throw new RuntimeException("Member not found with email: " + email);
        }
        member.setPhone(AESUtil.encrypt(updateMemberDto.getPhone()));
        member.setAddress(AESUtil.encrypt(updateMemberDto.getAddress()));
        return memberRepository.save(member); // 변경된 정보 저장
    }

    // 비밀번호 변경
    @Transactional
    public boolean changePassword(String email, String oldPassword, String newPassword) {
        Member member = memberRepository.findByEmail(AESUtil.encrypt(email));
        if (member != null && passwordEncoder.matches(oldPassword, member.getPassword())) {
            member.setPassword(passwordEncoder.encode(newPassword));
            memberRepository.save(member);
            return true; // 비밀번호 변경 성공
        }
        return false; // 잘못된 현재 비밀번호
    }

    // ID로 회원 조회
    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }

}
