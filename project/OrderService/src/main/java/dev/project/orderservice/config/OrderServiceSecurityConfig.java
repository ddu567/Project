package dev.project.orderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ContentSecurityPolicyHeaderWriter;
import org.springframework.security.web.header.writers.PermissionsPolicyHeaderWriter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;

@Configuration
@EnableWebSecurity
public class OrderServiceSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 보호를 비활성화
                .headers(headers -> {
                    headers.addHeaderWriter(new ContentSecurityPolicyHeaderWriter("script-src 'self';"));  // CSP 설정
                    headers.addHeaderWriter(new PermissionsPolicyHeaderWriter("frame-ancestors 'none'"));  // 프레임 옵션 설정
                    headers.addHeaderWriter(new StaticHeadersWriter("Referrer-Policy", "no-referrer"));  // Referrer Policy 설정
                })
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/**").authenticated() // "/api/**" 경로에 대해 인증된 사용자만 접근을 허용
                        .anyRequest().permitAll()) // 그 외의 요청에 대해서는 모두 접근을 허용
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults())) // OAuth2 리소스 서버로 JWT를 사용하여 인증 구성
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // 세션을 사용하지 않고 상태 없는 인증을 사용
        return http.build();
    }

}