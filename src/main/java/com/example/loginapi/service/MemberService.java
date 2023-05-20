package com.example.loginapi.service;

import com.example.loginapi.domain.Member;
import com.example.loginapi.jwt.util.RedisUtil;
import com.example.loginapi.repository.MemberRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final RedisUtil redisUtil;

    @Transactional
    public Member findByEmail(String email){
        return memberRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다."));
    }

    @Transactional
    public boolean IsPresentEmail(String email){
        Optional<Member> member= memberRepository.findByEmail(email);
        if(member.isPresent()){
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    public Member addMember(Member member){
        return memberRepository.save(member);
    }

    @Transactional
    public HttpHeaders setHeaderAccessToken(String accessToken){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization","Bearer "+accessToken);
        return httpHeaders;
    }

    @Transactional
    public void setCookieRefreshToken(HttpServletResponse response, String refreshToken){
        Cookie cookie = new Cookie("RefreshToken",refreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
//        cookie.setSecure(true); //-> https에서만 가능하게
        response.addCookie(cookie);
    }

    @Transactional
    public void deleteRefresh(String memberId){
        redisUtil.deleteData(memberId);
    }
}
