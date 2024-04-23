package dev.project.dto;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberDto {

    private String email;
    private String password;
    private String name;
    private String address;
}
