package com.example.loginapi.jwt.UserDetailsToken;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;


public class Role implements GrantedAuthority {
    public static final String USER = "USER";
    public static final String ADMIN = "ADMIN";

    private String authority;

    public Role(String authority){
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return authority;
    }

}
