package com.example.loginapi.jwt.filter;

// 이 필터에서는 UsernamePasswordAuthenticationFilter 이전에 처리되어야할 customFilter
// 헤더의 Authorization 부분의 Barrer 부분을 제외한 AccessToken 부분을 받아서
// 주입받은 Manager를 통해 Authenticate() 하여 맞는 AccessToken 인지 인증하는 필터 적용

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


// -> 왜 CustomFilter를 만들었냐? = 로그인폼 아니고 JWT 방식으로 로그인 활용하려고,
// -> 그럼 첨부터 CustomFilter 하지, 왜 Username~ Filter 쓰냐? = Username~ Filter를 이용한 기본적으로는 사용하기 위해서
@RequiredArgsConstructor
public class JwtAuthenticationCustomFilter extends OncePerRequestFilter {
    private final AuthenticationManager authenticationManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


    }
}
