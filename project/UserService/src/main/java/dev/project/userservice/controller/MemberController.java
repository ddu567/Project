package dev.project.userservice.controller;

import dev.project.userservice.dto.ChangePasswordRequest;
import dev.project.userservice.dto.LoginRequest;
import dev.project.userservice.dto.MemberDto;
import dev.project.userservice.dto.UpdateMemberDto;
import dev.project.userservice.entity.Member;
import dev.project.userservice.security.AESUtil;
import dev.project.userservice.security.JwtUtil;
import dev.project.userservice.service.MemberService;
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
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    // 회원가입 처리
    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody MemberDto memberDto, @RequestParam String code) {
        log.debug("회원가입 요청: 이메일={}, 코드={}", memberDto.getEmail(), code);
        boolean isRegistered = memberService.registerNewMember(memberDto, code);
        if (isRegistered) {
            return ResponseEntity.ok("회원 등록이 성공적으로 완료되었습니다.");
        }
        log.debug("회원가입 실패: 잘못된 인증 코드");
        return ResponseEntity.badRequest().body("잘못된 인증 코드입니다.");
    }

    // 로그인
    @PostMapping("/signin")
    public ResponseEntity<String> signIn(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(AESUtil.encrypt(loginRequest.getEmail()), loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtil.createJwt("USER", loginRequest.getEmail(), 3600000L); // 1 hour expiry

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + jwt); // 'Bearer'는 토큰 유형을 명시합니다.
            return new ResponseEntity<>(jwt, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("로그인 실패: " + e.getMessage());
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

    // 비밀번호 변경 요청 처리
    @PatchMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request) {
        boolean isChanged = memberService.changePassword(request.getEmail(), request.getOldPassword(), request.getNewPassword());
        if (isChanged) {
            return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("비밀번호 변경 실패: 잘못된 현재 비밀번호입니다.");
        }
    }
}
