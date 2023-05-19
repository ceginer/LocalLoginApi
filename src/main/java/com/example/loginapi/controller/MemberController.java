package com.example.loginapi.controller;

import com.example.loginapi.domain.Member;
import com.example.loginapi.dto.Login.MemberLoginDto;
import com.example.loginapi.dto.Login.MemberLoginResponseDto;
import com.example.loginapi.dto.Signup.MemberSignupDto;
import com.example.loginapi.dto.Signup.MemberSignupResponseDto;
import com.example.loginapi.jwt.UserDetailsToken.Details;
import com.example.loginapi.jwt.UserDetailsToken.DetailsService;
import com.example.loginapi.jwt.UserDetailsToken.Role;
import com.example.loginapi.jwt.provider.JwtAuthenticationProvider;
import com.example.loginapi.jwt.util.RedisUtil;
import com.example.loginapi.repository.MemberRepository;
import com.example.loginapi.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")

public class MemberController {
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final DetailsService DetailsService;
    private final RedisUtil redisUtil;
    private final AuthenticationManager authenticationManager;
    @PostMapping("/signup")
    public ResponseEntity signup(@RequestBody @Valid MemberSignupDto memberSignupDto, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            log.error("binding result 에러");
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        // 이미 존재하는 이메일인지 검증
        if( memberService.IsPresentEmail(memberSignupDto.getEmail()) ){
            log.error("이미 존재하는 이메일인 에러");
            return new ResponseEntity(HttpStatus.CONFLICT); // 에러 409, 이미 있는 이메일.
        }

        Member member = new Member();
        member.setName(memberSignupDto.getName());
        member.setEmail(memberSignupDto.getEmail());
        member.setPassword(passwordEncoder.encode(memberSignupDto.getPassword()));
        member.setRole(Role.ADMIN);

        Member saveMember = memberService.addMember(member);
        // Spring Data Jpa 통해 MemberRepository.save 할 수도 있지만,
        // 추가로 role 권한을 설정해줘야 하기 때문에 Service에서 수행
        // -> 물론 MemberRepository.save 하기만 해도 Service로 분리하는 것이 좋음.


        // 테스트용 response가 잘 받아오는지 -> responsedto 객체를 json 으로 바꿔주는 라이브러리 필요 = Jackson
        MemberSignupResponseDto memberSignupResponseDto = new MemberSignupResponseDto();
        memberSignupResponseDto.setMemberId(saveMember.getMemberId());
        memberSignupResponseDto.setEmail(saveMember.getEmail());
        memberSignupResponseDto.setPassword(saveMember.getPassword());
        // Member 객체가 만들어지면서 필드의 regdate 가 자동 생성됨.
        memberSignupResponseDto.setRegdate(saveMember.getRegdate());

        // 회원가입
        return new ResponseEntity(memberSignupResponseDto, HttpStatus.OK);


    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid MemberLoginDto memberLoginDto, BindingResult bindingResult, HttpServletResponse response){
        if(bindingResult.hasErrors()){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        // ----모두 일치하는 경우-----
        UsernamePasswordAuthenticationToken token
                = new UsernamePasswordAuthenticationToken(memberLoginDto.getEmail(), memberLoginDto.getPassword());
        // 로그인할 때는 email과 password로만 이루어진 token 만들기
        Authentication authentication = authenticationManager.authenticate(token);

//
//        // Controller 에서는 Member 바로 사용
//        Member member = memberService.findByEmail(memberLoginDto.getEmail());
//        // -> Service에서 따로 일치하는 email이 없을 경우 exception 발생
//        if(!passwordEncoder.matches(memberLoginDto.getPassword(),member.getPassword())){
//            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
//        } // 비밀번호 불일치시, UNAUTHORIZED 반환

        //모두 일치하면 access, refreshToken 발급
        // 인증 토큰 발급할 때,
        String accessToken = jwtAuthenticationProvider.createAccessToken(authentication);
        String refreshToken = jwtAuthenticationProvider.createRefreshToken(authentication);

        // RefreshToken을 DB에 저장한다. 성능 때문에 DB가 아니라 Redis에 저장하는 것이 좋다.
        // 그리고 redis에 저장하면서 사용자의 Id 를 같이 저장한다.
        Details userDetails = (Details) authentication.getPrincipal();
        Member member = userDetails.getMember();

        jwtAuthenticationProvider.setRefreshToken(refreshToken, String.valueOf(member.getMemberId()));
        // Id는 long 형태이므로

        // AccessToken은 헤더의 Authorization 의 Barrer 뒤에 토큰 발급
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization","Bearer "+accessToken);

        // RefreshToken은 브라우저의 쿠키에 지정하여 보낸다.

        Cookie cookie = new Cookie("RefreshToken",refreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
//        cookie.setSecure(true); //-> https에서만 가능하게
        response.addCookie(cookie);

        // 테스트용 잘되었는지
        MemberLoginResponseDto loginResponse = MemberLoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .memberId(member.getMemberId())
                .nickname(member.getName())
                .build();

        return new ResponseEntity( loginResponse, httpHeaders, HttpStatus.OK);
//        return new ResponseEntity<>(accessToken, httpHeaders, HttpStatus.OK);
        // 헤더에 acceessToken, 쿠키에 ResponseToken 담아서, body에는 테스트용
    }


}
