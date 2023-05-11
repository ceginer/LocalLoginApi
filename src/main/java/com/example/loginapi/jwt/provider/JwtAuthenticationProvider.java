package com.example.loginapi.jwt.provider;

import com.example.loginapi.jwt.util.JwtTokenizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


// Provider 의 역할
// JwtToken create(만들기), authenticate(검증)
@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider {

    private String secretKey;
    private final JwtTokenizer jwtTokenizer;




}
