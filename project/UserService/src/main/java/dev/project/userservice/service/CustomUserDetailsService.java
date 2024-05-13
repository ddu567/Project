package dev.project.userservice.service;

import dev.project.userservice.entity.Member;
import dev.project.userservice.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);
    @Autowired
    private MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 이메일을 통해 사용자 정보 조회
        Member memberData = memberRepository.findByEmail(email);

        if (memberData != null) {
            // 사용자 정보가 존재하면 UserDetails 객체를 생성하여 반환
            return new CustomUserDetails(memberData);
        }

        throw new UsernameNotFoundException("User not found with email: " + email); // 사용자가 없을 경우 예외 발생
    }
}
