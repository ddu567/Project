package dev.project.userservice.service;

import dev.project.userservice.entity.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final Member member; // 회원 정보를 저장하는 멤버 변수

    public CustomUserDetails(Member member) {
        this.member = member; // 생성자를 통해 멤버 인스턴스 초기화
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(); // 사용자 권한 목록 반환
    }

    @Override
    public String getPassword() {
        return member.getPassword(); // 사용자 비밀번호 반환
    }

    @Override
    public String getUsername() {
        return member.getEmail(); // 사용자 이메일(아이디) 반환
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 여부 반환
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정 잠김 여부 반환
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 자격 증명 만료 여부 반환
    }

    @Override
    public boolean isEnabled() {
        return true; // 계정 활성화 여부 반환
    }
}
