package com.example.loginapi.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationManagerConfig authenticationManagerConfig;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    // ManagerConfig 가 Manager 역할을 할 수 있게끔하고,
    // EntryPoint 가 에러가 일어났을 때, 어떤 것들을 할 수 있는지를 정해주도록 하기 위해서
    // -> 기본적인 세팅으로, 외워야 할 것들이 아닌 기본적인 방법들? 이라고 생각하면 될 듯




}
