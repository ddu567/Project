package dev.project.userservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerificationRequest {

    private String email; // 사용자 이메일
    private String verificationCode; // 인증 코드

}