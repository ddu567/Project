package dev.project.userservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;

// JWT 생성 및 검증 유틸리티 클래스
@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    private final SecretKey secretKey;

    // 시크릿 키를 디코드하여 생성자에서 초기화
    public JwtUtil(@Value("${spring.jwt.secret}") String secret) {
        byte[] decodedKey = Base64.getDecoder().decode(secret);
        this.secretKey = new SecretKeySpec(decodedKey, "HmacSHA256");
    }

    // JWT 생성 메소드
    public String createJwt(String category, String email, Long expiredMs) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiredMs);
        return Jwts.builder()
                .claim("category", category)
                .claim("email", email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰 만료 여부 확인 메소드
    public boolean isExpired(String token) {
        return parseClaims(token).getExpiration().before(new Date());
    }

    // 토큰에서 이메일 추출 메소드
    public String getEmail(String token) {
        return parseClaims(token).get("email", String.class);
    }

    // 토큰에서 카테고리 추출 메소드
    public String getCategory(String token) {
        return parseClaims(token).get("category", String.class);
    }

    private Claims parseClaims(String token) {
        return Jwts.parser().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
    }


}
