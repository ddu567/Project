package dev.project.userservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public LoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String email = obtainUsername(request);
        String password = obtainPassword(request);
        // 이메일과 비밀번호를 사용하여 인증 시도
        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        String email = ((UserDetails) authentication.getPrincipal()).getUsername();
        // 각 토큰 생성
        String accessToken = jwtUtil.createJwt("access", email, 3600000L);  // 1시간 동안 유효한 액세스 토큰
        String refreshToken = jwtUtil.createJwt("refresh", email, 86400000L);  // 1일 동안 유효한 리프레시 토큰
        response.setHeader("Authorization", "Bearer " + accessToken);
        response.addCookie(createRefreshTokenCookie(refreshToken));
    }

    // 리프레시 토큰을 쿠키로 설정
    private Cookie createRefreshTokenCookie(String refreshToken) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setMaxAge(86400); // 1일
        cookie.setHttpOnly(true);
        return cookie;
    }
}
