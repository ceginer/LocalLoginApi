package com.example.loginapi.config;

import com.example.loginapi.OAuth.Service.CustomOAuth2UserService;
import com.example.loginapi.OAuth.handler.OAuth2AuthenticationSuccessHandler;
import com.example.loginapi.OAuth.handler.OAuth2LoginFailureHandler;
import com.example.loginapi.jwt.exception.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationManagerConfig authenticationManagerConfig;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    private final AccessDeniedHandler jwtAccessDeniedHandler;
    private final OAuth2AuthenticationSuccessHandler oAuth2SuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
    private final CustomOAuth2UserService customOAuth2UserService;

    // ManagerConfig 가 Manager 역할을 할 수 있게끔하고,
    // EntryPoint 가 에러가 일어났을 때, 어떤 것들을 할 수 있는지를 정해주도록 하기 위해서
    // -> 기본적인 세팅으로, 외워야 할 것들이 아닌 기본적인 방법들? 이라고 생각하면 될 듯
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
         http
                 // 먼저 cors 설정부터 해야한다. -> preflight 에 대해서 오류를 도출하지 않기 위해서
//                 .cors()
//                 .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                // 서버에서 session 허용 X -> stateless

                // formLogin 과 basic 을 이용하지 않을으로써 기존 filter 의 역할을 사용하지 않을 것임.
                // 여기서는 UsernamePasswordFilter 의 기본 필터 대신 custom filter 적용시킬 것.
                // 그 이유는 JwtAuthenticationCustomFilter 에서 설명하겠음.

                .and()
                .formLogin().disable()
                .httpBasic().disable()
                .csrf().disable() // 일단 csrf 비활성화인데, 귀찮아서 해놓은것이라 함.
                // 아마 customfilter 에서 OncePerRequestFilter 가 있기 때문에 csrf 공격을 막을 수 있을듯해서 그런 듯.
//                .cors()

                // AuthenticationManager 를 통해 기존 filter 대체
                .apply(authenticationManagerConfig) // configure 메서드를 통해 HttpSecurity 인 http 객체를 설정해줘야 함.


                // 접근 권한 설정 (pre-flight, cors설정)
                .and()
                .authorizeRequests((authz) -> authz
                        .requestMatchers("/members/signup").permitAll()
                        .requestMatchers("/members/login", "/members/refreshToken","/members/logout","/abc","/index.html","/error","/").permitAll()
                        .requestMatchers("/manager/**").hasRole("ADMIN")
                        .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                        .anyRequest().permitAll()
//                        .anyRequest().authenticated()
                )

                //.requestMatchers("/manager로 시작하는 url") ADMIN 이 주어졌을 때 접근 허용 및 hasAnyRole 필요

                // Preflight 요청은 허용한다. https://velog.io/@jijang/%EC%82%AC%EC%A0%84-%EC%9A%94%EC%B2%AD-Preflight-request
                .exceptionHandling()
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                // -> 인증되지 않은 사용자에 대한 접근처리
                .accessDeniedHandler(jwtAccessDeniedHandler)
                // -> 인증되었으나, 권한이 없는 사용자에 대한 처리

                //-----------Oauth---------
                .and()
                .oauth2Login(oauth2 -> oauth2
//                        .authorizationEndpoint(authorization -> authorization
//                                .baseUri("/oauth2/authorization")
//                        )
                        .authorizationEndpoint(authorization -> authorization
                                .baseUri("/oauth/api")
                        )
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler(oAuth2LoginFailureHandler)
                );
//                .oauth2Login()
//                .authorizationRequestRepository(cookieAuthorizationRequestRepository)
//                .and()
//                .successHandler(oAuth2SuccessHandler) // 동의하고 계속하기를 눌렀을 때 Handler 설정 , 여기선 안함
//                .failureHandler(oAuth2LoginFailureHandler) // 소셜 로그인 실패 시 핸들러 설정
//                .userInfoEndpoint().userService(customOAuth2UserService);
         return http.build();// customUserService 설정

//                .oauth2Login()
//                .authorizationRequestRepository(cookieAuthorizationRequestRepository)
//                .successHandler(oAuth2SuccessHandler) // 동의하고 계속하기를 눌렀을 때 Handler 설정 , 여기선 안함
//                .failureHandler(oAuth2LoginFailureHandler) // 소셜 로그인 실패 시 핸들러 설정
//                .userInfoEndpoint().userService(customOAuth2UserService)
//                .and()// customUserService 설정
//                .authorizationRequestRepository(cookieAuthorizationRequestRepository)
//                .successHandler(oAuth2SuccessHandler) // 동의하고 계속하기를 눌렀을 때 Handler 설정 , 여기선 안함
//                .failureHandler(oAuth2LoginFailureHandler) // 소셜 로그인 실패 시 핸들러 설정
//                .userInfoEndpoint().userService(customOAuth2UserService)// customUserService 설정
//                .oauth2Login(oauth2 -> oauth2
//                        .authorizationEndpoint(authorization -> authorization
//                                .baseUri("/oauth2/authorization")
//                                .authorizationRequestRepository(this.authorizationRequestRepository())
//                        )
//                        .redirectionEndpoint(redirection -> redirection
//                                .baseUri("/*/oauth2/code/*")
//                        )
//                        .userInfoEndpoint(userInfo -> userInfo
//                                .userService(this.oauth2UserService())
//                        )
//                        .successHandler(oAuth2AuthenticationSuccessHandler())
//                        .failureHandler(oAuth2AuthenticationFailureHandler())
//                );
//                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }



//    @Bean
//    CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(Arrays.asList("https://example.com"));
//        configuration.setAllowedMethods(Arrays.asList("GET","POST"));
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
}
