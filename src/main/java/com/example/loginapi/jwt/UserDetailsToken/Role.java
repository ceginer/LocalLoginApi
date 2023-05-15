package com.example.loginapi.jwt.UserDetailsToken;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@Getter
@RequiredArgsConstructor
public enum Role{

    ADMIN("관리자"), USER("일반 사용자");

    private final String title;

}
