package dev.project.userservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {

    private String email; // 사용자 이메일
    private String oldPassword; // 현재 비밀번호
    private String newPassword; // 새 비밀번호
}
