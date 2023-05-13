package com.example.loginapi.dto.Login;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberLoginDto {
    // 형식에 맞추는 건 프론트에서 이미 처리했다고 함.
    @NotEmpty
    @Pattern(regexp = "^[a-zA-Z0-9+-\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$")
    private String email;

    @NotEmpty
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!])(?=\\\\S+$).{8,20}$")
    private String password;
}
