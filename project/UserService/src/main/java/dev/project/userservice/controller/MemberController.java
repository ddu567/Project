package dev.project.userservice.controller;

import dev.project.userservice.dto.LoginRequest;
import dev.project.userservice.dto.MemberDto;
import dev.project.userservice.dto.UpdateMemberDto;
import dev.project.userservice.entity.Member;
import dev.project.userservice.service.MailService;
import dev.project.userservice.service.MemberService;
import dev.project.userservice.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private static final Logger log = LoggerFactory.getLogger(MemberController.class);

    @Autowired
    private MemberService memberService;

    @Autowired
    private MailService emailService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<String> signUpProcess(@Valid @RequestBody MemberDto memberDto) {
        memberService.registerNewMember(memberDto);
        return ResponseEntity.ok("Signup successfully");
    }

    // 로그인
    @PostMapping("/signin")
    public ResponseEntity<String> signIn(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtil.createJwt("USER", loginRequest.getEmail(), 3600000L); // 1 hour expiry

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + jwt); // 'Bearer'는 토큰 유형을 명시합니다.
            return new ResponseEntity<>(jwt, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Login failed: " + e.getMessage());
        }
    }

    // 로그아웃
    @PostMapping("/signout")
    public ResponseEntity<String> logout() {
        log.info("Attempting to log out");
        // Perform logout logic here
        log.info("Logged out successfully");
        return ResponseEntity.ok("Logged out successfully");
    }

    // 회원 정보 수정 (전화번호, 주소)
    @PatchMapping("/update/{email}")
    public ResponseEntity<Member> updateMember(@PathVariable String email, @RequestBody UpdateMemberDto updateMemberDto) {
        Member updatedMember = memberService.updateMember(email, updateMemberDto);
        return ResponseEntity.ok(updatedMember);
    }
}
