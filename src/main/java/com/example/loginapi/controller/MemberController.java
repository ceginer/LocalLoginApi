package com.example.loginapi.controller;

import com.example.loginapi.domain.Member;
import com.example.loginapi.dto.Login.MemberLoginDto;
import com.example.loginapi.dto.Signup.MemberSignupDto;
import com.example.loginapi.dto.Signup.MemberSignupResponseDto;
import com.example.loginapi.jwt.UserDetailsToken.DetailsService;
import com.example.loginapi.jwt.provider.JwtAuthenticationProvider;
import com.example.loginapi.repository.MemberRepository;
import com.example.loginapi.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/members")

public class MemberController {
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final DetailsService DetailsService;

    @PostMapping("/signup")
    public ResponseEntity signup(@RequestBody @Valid MemberSignupDto memberSignupDto, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        Member member = new Member();
        member.setName(memberSignupDto.getName());
        member.setEmail(memberSignupDto.getName());
        member.setPassword(passwordEncoder.encode(memberSignupDto.getPassword()));

        Member saveMember = memberService.addMember(member);
        // Spring Data Jpa 통해 MemberRepository.save 할 수도 있지만,
        // 추가로 role 권한을 설정해줘야 하기 때문에 Service에서 수행
        // -> 물론 MemberRepository.save 하기만 해도 Service로 분리하는 것이 좋음.

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
    public ResponseEntity login(@RequestBody @Valid MemberLoginDto memberLoginDto, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        Member member = memberService.findByEmail(memberLoginDto.getEmail());
        // -> Service에서 따로 일치하는 email이 없을 경우 exception 발생
        if(passwordEncoder.matches(memberLoginDto.getPassword(),member.getPassword())){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        } // 비밀번호 불일치

        UsernamePasswordAuthenticationToken token
                = new UsernamePasswordAuthenticationToken(member.getEmail(), member.getPassword());
        // 로그인할 때는 email과 password로만 이루어진 token 만들기

        //모두 일치하면 access, refreshToken 발급
        String accessToken = jwtAuthenticationProvider.createAccessToken(token);
        String refreshToken = jwtAuthenticationProvider.createRefreshToken(token);

        // AccessToken은 헤더의 Authorization 의 Barrer 뒤에 토큰 발급
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add();

        // RefreshToken을 DB에 저장한다. 성능 때문에 DB가 아니라 Redis에 저장하는 것이 좋다.
        // 그리고 redis에 저장하면서 사용자의 clientip 를 같이 저장한다.
        //----------> 코드 필요




    }


}
