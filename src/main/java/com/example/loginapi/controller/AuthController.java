package com.example.loginapi.controller;

import com.example.loginapi.Response.BaseResponse;
import com.example.loginapi.Response.BaseResponseStatus;
import com.example.loginapi.domain.Member;
import com.example.loginapi.dto.Signup.MemberSignupDto;
import com.example.loginapi.jwt.UserDetailsToken.Details;
import com.example.loginapi.jwt.UserDetailsToken.DetailsService;
import com.example.loginapi.jwt.provider.JwtAuthenticationProvider;
import com.example.loginapi.jwt.util.RedisUtil;
import com.example.loginapi.service.MemberService;
import com.sun.net.httpserver.Authenticator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth")

public class AuthController {//

    // https://localhost:8080/swagger-ui/index.html -> swagger 주소
    // https://localhost:8080/oauth/api/{원하는 소셜 ex>kakao, naver, google} -> 애 접속하면 리다이렉트 되는 url

    // https://localhost:8080/oauth2/authorization/kakao


    @GetMapping("/test")
    public BaseResponse<Member> test(){
        Details userDetails = (Details) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member userInfo = userDetails.getMember();
        return new BaseResponse<Member>(BaseResponseStatus.SUCCESS, userInfo);
    }

}