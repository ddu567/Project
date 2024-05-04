package dev.project.userservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

@Controller
public class MainController {
    public ResponseEntity<String> mainP() {
        // 현재 사용자
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok("Main Page");
    }
}
