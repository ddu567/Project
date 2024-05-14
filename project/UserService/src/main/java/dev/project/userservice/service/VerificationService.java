package dev.project.userservice.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String VERIFICATION_CODE_KEY_PREFIX = "verification-code:";

    // 인증 코드 생성
    public String generateVerificationCode() {
        return String.valueOf((int) (Math.random() * 9000) + 1000);
    }

    // 인증 코드를 Redis에 저장
    public void saveVerificationCode(String email, String verificationCode) {
        String key = VERIFICATION_CODE_KEY_PREFIX + email;
        redisTemplate.opsForValue().set(key, verificationCode, 15, TimeUnit.MINUTES);
        log.debug("인증 코드 저장: 키 = {}, 값 = {}", key, verificationCode);
    }

    // 인증 코드 검증
    public String getVerificationCode(String email) {
        String key = VERIFICATION_CODE_KEY_PREFIX + email;
        String code = redisTemplate.opsForValue().get(key);
        log.debug("인증 코드 조회: 키 = {}, 값 = {}", key, code);
        return code;
    }

}
