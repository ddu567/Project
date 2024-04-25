package dev.project.member.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

@Controller
public class MainController {
    public String mainP() {
        // 현재 사용자
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return "index";
    }
}
