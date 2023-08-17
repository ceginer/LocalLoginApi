package com.example.loginapi.OAuth.handler;

import com.example.loginapi.domain.Member;
import com.example.loginapi.jwt.UserDetailsToken.Details;
import com.example.loginapi.jwt.provider.JwtAuthenticationProvider;
import com.example.loginapi.service.MemberService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

//    @Value("${oauth.authorizedRedirectUri}")
//    private String redirectUri;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final MemberService memberService;
//    private final CookieAuthorizationRequestRepository cookieAuthorizationRequestRepository;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        log.info("소셜 로그인 성공");

        String accessTokenString = jwtAuthenticationProvider.createAccessToken(authentication);
        String refreshTokenString = jwtAuthenticationProvider.createRefreshToken(authentication);

        Details userDetails = (Details) authentication.getPrincipal();
        Member member = userDetails.getMember();

        // redis 에 저장
        jwtAuthenticationProvider.setRefreshToken(String.valueOf(member.getMemberId()), refreshTokenString);
//
        // AccessToken은 헤더의 Authorization 의 Barrer 뒤에 토큰을 붙혀 response 보내기
//        HttpHeaders httpHeaders = memberService.setHeaderAccessToken(accessTokenString);
        response.addHeader("Authorization","Bearer " + accessTokenString);
        // RefreshToken은 브라우저의 쿠키에 지정하여 보낸다.
        memberService.setCookieRefreshToken(response, refreshTokenString);

        response.sendRedirect("https://localhost:8080/oauth/test");


//        if(oAuth2User.getRole() == Role.GUEST) {
//            String accessToken = jwtService.createAccessToken(oAuth2User.getEmail());
//            response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
//            response.sendRedirect("oauth2/sign-up"); // 프론트의 회원가입 추가 정보 입력 폼으로 리다이렉트
//
//            jwtService.sendAccessAndRefreshToken(response, accessToken, null);
////                User findUser = userRepository.findByEmail(oAuth2User.getEmail())
////                                .orElseThrow(() -> new IllegalArgumentException("이메일에 해당하는 유저가 없습니다."));
////                findUser.authorizeUser();
//        } else {
//            loginSuccess(response, oAuth2User); // 로그인에 성공한 경우 access, refresh 토큰 생성
//        }
//
//        String targetUrl = determineTargetUrl(request, response, authentication);
//
//        if (response.isCommitted()) {
//            log.debug("Response has already been committed.");
//            return;
//        }
//        clearAuthenticationAttributes(request, response);
//        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

}
