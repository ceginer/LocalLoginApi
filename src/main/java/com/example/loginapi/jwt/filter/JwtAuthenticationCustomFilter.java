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
import org.springframework.security.core.context.SecurityContextHolder;
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

    // Filter는 Access토큰이 존재할 때만 사용되는 것.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessTokenString = resolveToken(request);
        if (accessTokenString != null){
            Claims claims = provider.validateAccessToken(accessTokenString); // 실패 시 에러 발생
            UsernamePasswordAuthenticationToken token = provider.getToken(claims);
            // -> Manager.authenticate 기능을 모두 수행해버려서 authenticate 필요없음
            // sub에 암호화된 데이터를 집어넣고, 복호화하는 코드를 넣어줄 수 있다.
            //(여긴안한거)

            // 인증정보가 SecurityContextHolder 에 저장되게 됨
            SecurityContextHolder.getContext()
                    .setAuthentication(token); // 현재 요청에서 언제든지 인증정보를 꺼낼 수 있도록 해준다.
        }
        filterChain.doFilter(request, response);
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
