package dev.project.controller;

import dev.project.dto.LoginRequest;
import dev.project.dto.MemberDto;
import dev.project.dto.UpdateMemberDto;
import dev.project.entity.Member;
import dev.project.service.MailService;
import dev.project.service.MemberService;
import dev.project.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Controller
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

    @Autowired
    private HttpServletRequest request;

    // 회원가입
    @GetMapping("/signup")
    public String signUpP(Model model) {
        model.addAttribute("member", new MemberDto());
        return "member/signup";
    }

    @PostMapping(value = "/signup", consumes = "application/json")
    public String signUpProcess(@Valid @RequestBody MemberDto memberDto) {
        memberService.registerNewMember(memberDto);
        return "redirect:/signin";
    }

    // 로그인
    @GetMapping("/signin")
    public String signInP() {
        return "member/signin";
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            // Assume each user has a role or category, here simplified as "USER"
            String jwt = jwtUtil.createJwt("USER", loginRequest.getEmail(), 3600000L); // 1 hour expiry

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);

            return ResponseEntity.ok().headers(headers).body("Login successful");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Login failed: " + e.getMessage());
        }
    }

    // 로그아웃
    @PostMapping("/signout")
    public ResponseEntity<?> logout() {
        log.info("Attempting to log out");
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            log.info("Logging out with token: {}", token);
        }
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
