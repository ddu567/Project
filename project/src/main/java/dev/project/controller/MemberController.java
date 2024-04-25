package dev.project.controller;

import dev.project.dto.MemberDto;
import dev.project.service.MailService;
import dev.project.service.MemberService;
import dev.project.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String signInProcess(@RequestParam String email, @RequestParam String password) {

        log.debug("Login attempt with email: {}", email);
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return "redirect:/";
        } catch (AuthenticationException e) {
            log.error("Login failed for user: {}", email, e);
            return "redirect:/signin?error=true";
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
}
