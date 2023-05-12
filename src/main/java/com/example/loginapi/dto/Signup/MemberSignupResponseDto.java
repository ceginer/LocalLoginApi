package com.example.loginapi.dto.Signup;

import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Setter
public class MemberSignupResponseDto {
    private Long memberId;
    private String email;
    private String password;
    private LocalDateTime regdate;
}
