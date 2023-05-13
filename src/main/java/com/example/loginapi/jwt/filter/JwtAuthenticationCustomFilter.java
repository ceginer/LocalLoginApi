package com.example.loginapi.jwt.filter;

// 이 필터에서는 UsernamePasswordAuthenticationFilter 이전에 처리되어야할 customFilter
// 헤더의 Authorization 부분의 Barrer 부분을 제외한 AccessToken 부분을 받아서
// 주입받은 Manager를 통해 Authenticate() 하여 맞는 AccessToken 인지 인증하는 필터 적용

import com.example.loginapi.jwt.provider.JwtAuthenticationProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


// -> 왜 CustomFilter를 만들었냐? = 로그인폼 아니고 JWT 방식으로 로그인 활용하려고,
// -> 그럼 첨부터 CustomFilter 하지, 왜 Username~ Filter 쓰냐? = Username~ Filter를 이용한 기본적으로는 사용하기 위해서
@RequiredArgsConstructor
public class JwtAuthenticationCustomFilter extends OncePerRequestFilter {
    private final JwtAuthenticationProvider provider;
    // 원래 같으면 util 의 Tokenizer 클래스에 UsernameToken을 만들기까지의 과정을 넣고,
    // 이 클래스에서는 Manager.authenticate() 만 하면 완성될 수 있도록 하면 더 좋다.

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessTokenString = resolveToken(request);
        if (accessTokenString != null){
            Claims claims = provider.validateAccessToken(accessTokenString); // 실패 시 에러 발생
            UsernamePasswordAuthenticationToken token = provider.getToken(claims);
            Authentication authentication = provider.getAuthentication(accessTokenString);
        }
    }





    /**
     * http 헤더로부터 bearer 토큰을 가져옴.
     * @param requset
     * @return
     */
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null; // 아무것도 받지 못했을 때, ( header에 아무것도 없을 때 )
    }
}
