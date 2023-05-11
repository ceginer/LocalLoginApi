package com.example.loginapi.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsUtils;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationManagerConfig authenticationManagerConfig;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    // ManagerConfig 가 Manager 역할을 할 수 있게끔하고,
    // EntryPoint 가 에러가 일어났을 때, 어떤 것들을 할 수 있는지를 정해주도록 하기 위해서
    // -> 기본적인 세팅으로, 외워야 할 것들이 아닌 기본적인 방법들? 이라고 생각하면 될 듯

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // 서버에서 session 허용 X
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)


                // formLogin 과 basic 을 이용하지 않을으로써 기존 filter 의 역할을 사용하지 않을 것임.
                // 여기서는 UsernammPasswordFilter 의 기본 필터 대신 custom filter 적용시킬 것.
                // 그 이유는 authenticationManagerConfig 에서 설명하겠음.
                .and()
                .formLogin().disable()
                .httpBasic().disable()
                .csrf().disable() // 일단 csrf 비활성화인데, 귀찮아서 해놓은것이라 함. 나중에 수정
//                .cors()

                // AuthenticationManager 를 통해 기존 filter 대체
                .apply(authenticationManagerConfig) // configure 메서드를 통해 HttpSecurity 인 http 객체를 설정해줘야 함.


                // 접근 권한 설정 (pre-flight, cors설정)
                .and()
                .authorizeRequests()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
        // Preflight 요청은 허용한다. https://velog.io/@jijang/%EC%82%AC%EC%A0%84-%EC%9A%94%EC%B2%AD-Preflight-request

                .mvcMatchers("/members/signup", "/members/login", "/members/refreshToken").permitAll()
//                .mvcMather(GET, "/~~~") Role이 주어졌을 때 접근 허용 및 hasAnyRole 필요

                .and()
                .exceptionHandling()
                .authenticationEntryPoint(customAuthenticationEntryPoint)

                .and()
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }



}
