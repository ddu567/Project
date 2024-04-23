package dev.project.controller;

import dev.project.dto.MemberDto;
import dev.project.service.MemberService;
import dev.project.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
public class MemberController {

    public class AuthenticationResponse {
        private final String jwt; // final 키워드를 추가하여 불변성을 보장합니다.

        public AuthenticationResponse(String jwt) {
            this.jwt = jwt;
        }

        public String getJwt() {
            return jwt;
        }
    }

    @Autowired
    private MemberService memberService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private HttpServletRequest request;

    @GetMapping("/signup")
    public String showSignUpForm(Model model) {
        model.addAttribute("member", new MemberDto());
        return "signup";
    }

    @PostMapping("/signup")
    public String signUpP(MemberDto memberDto) {
        memberService.registerNewMember(memberDto);
        return "redirect:/signin";
    }

    @GetMapping("/signin")
    public String signInP() {
        return "signin";
    }

    @PostMapping("/signin")
    public String login(@RequestParam String email, @RequestParam String password, HttpServletRequest request) {
        log.debug("Login attempt with email: {}", email);

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return "redirect:/home";
        } catch (AuthenticationException e) {
            log.error("Login failed for user: {}", email, e);
            return "redirect:/signin?error=true";
        }
    }


}



