package dev.project.security;

import dev.project.entity.Member;
import dev.project.service.CustomUserDetails;
import dev.project.service.CustomUserDetailsService;
import dev.project.service.MemberService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Enumeration;

public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final  CustomUserDetailsService customUserDetailsService;

    public JwtFilter(JwtUtil jwtUtil, CustomUserDetailsService customUserDetailsService) {

        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
    }

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        logger.debug("Authorization header: {}", authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            logger.debug("Extracted Token: {}", token);

            try {
                if (!jwtUtil.isExpired(token)) {
                    String email = jwtUtil.getEmail(token);
                    logger.debug("Token email: {}", email);

                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.debug("Authentication successful for: {}", email);
                } else {
                    logger.error("Token expired");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Token expired");
                    return;
                }
            } catch (Exception e) {
                logger.error("Token parsing error: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid Token");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }



//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        // 헤더에서 'Authorization' 키에 담긴 토큰을 꺼냄
//        String authHeader = request.getHeader("Authorization");
//        String accessToken = null;
//        System.out.println("Received Authorization Header: " + authHeader);
//
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            accessToken = authHeader.substring(7); // 'Bearer ' 제거
//            System.out.println("Extracted Token: " + accessToken);
//        } else { // 여기서 토큰 검증 로직 수행
//            System.out.println("No valid Authorization header found.");
//        }
//
//        // 토큰이 없다면 다음 필터로 넘김
//        if (accessToken == null) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        try {
//            // 토큰 만료 여부 확인
//            if (jwtUtil.isExpired(accessToken)) {
//                throw new ExpiredJwtException(null, null, "Access token expired");
//            }
//
//            // 토큰의 카테고리가 'access'인지 확인
//            String category = jwtUtil.getCategory(accessToken);
//            if (!"access".equals(category)) {
//                throw new IllegalArgumentException("Invalid access token");
//            }
//
//            // 유효한 토큰인 경우, 사용자 정보를 SecurityContext에 등록
//            String email = jwtUtil.getEmail(accessToken);
//            Member member = new Member();
//            member.setEmail(email);
//            CustomUserDetails customUserDetails = new CustomUserDetails(member);
//            Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
//            SecurityContextHolder.getContext().setAuthentication(authToken);
//        } catch (ExpiredJwtException | IllegalArgumentException e) {
//            // 토큰 관련 예외 처리
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.getWriter().print(e.getMessage());
//            return; // 필터 체인 진행 중단
//        }
//        filterChain.doFilter(request, response);
//    }



}
