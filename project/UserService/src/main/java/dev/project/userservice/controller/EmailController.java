package dev.project.userservice.controller;

import dev.project.userservice.dto.VerificationRequest;
import dev.project.userservice.service.EmailService;
import dev.project.userservice.service.VerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/members")
@RequiredArgsConstructor
public class EmailController {

    private final VerificationService verificationService;
    private final EmailService emailService;

    // 이메일로 인증 코드 발송
    @PostMapping("/send-verification-code")
    public ResponseEntity<String> sendEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = String.valueOf((int)((Math.random() * (9999 - 1000)) + 1000)); // 랜덤 코드 생성
        verificationService.saveVerificationCode(email, code);
        log.debug("발송된 인증 코드: {}", code);
        return ResponseEntity.ok("인증 코드가 성공적으로 발송되었습니다.");
    }

    // 이메일 인증 코드 검증
    @PostMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestBody VerificationRequest verificationRequest) {
        String email = verificationRequest.getEmail();
        String code = verificationRequest.getVerificationCode();
        String savedCode = verificationService.getVerificationCode(email);
        log.debug("Retrieved code from Redis: {} for email: {}", savedCode, email);
        if (savedCode != null && savedCode.equals(code)) {
            return ResponseEntity.ok("이메일 인증이 성공적으로 완료되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("잘못된 인증 코드입니다.");
        }
    }

}