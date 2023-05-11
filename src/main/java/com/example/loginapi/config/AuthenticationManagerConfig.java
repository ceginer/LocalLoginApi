package com.example.loginapi.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.security.authentication.AuthenticationProvider;
// AuthenticationProvider 이미 그런 클래스가 있기 때문에 앞에 Jwt 붙혀준 것임.


@Configuration
@RequiredArgsConstructor
public class AuthenticationManagerConfig extends AbstractHttpConfigurer<AuthenticationManagerConfig, HttpSecurity> {

    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    @Override
    public void configure(HttpSecurity builder) throws Exception {
        AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);

        builder.addFilterBefore(new JwtAuthenticationCustomFilter(authenticationManager),
                UsernamePasswordAuthenticationFilter.class)
                // customfilter (Manager가 주입된) 를 Username~ Filter전에 실행시키겠다.

                .authenticationProvider(jwtAuthenticationProvider);
                // 그 적용된 필터는 여러 provider 중에 내가 만든 jwtAuthenticationProvider 이다.
                // -> 결국 타고 들어가다보면 authenticationProviders.add(authenticationProvider); 이기 때문에
                    // Manager 가 여러개의 provider가 있더라도 관리할 수 있게끔 만들어주었다.

    }
}
