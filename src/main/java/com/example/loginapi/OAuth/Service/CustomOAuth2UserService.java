package com.example.loginapi.OAuth.Service;

import com.example.loginapi.OAuth.OAuthUserDto.*;
import com.example.loginapi.domain.Member;
import com.example.loginapi.jwt.UserDetailsToken.Details;
import com.example.loginapi.jwt.UserDetailsToken.Role;
import com.example.loginapi.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.security.AuthProvider;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

//    private static final String NAVER = "naver";
//    private static final String KAKAO = "kakao";

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        OAuth2UserInfo oAuth2UserInfo =null;

        if(userRequest.getClientRegistration().getRegistrationId().equals("google")) {
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        }
        else if(userRequest.getClientRegistration().getRegistrationId().equals("facebook")) {
            oAuth2UserInfo = new FacebookUserInfo(oAuth2User.getAttributes());
        }
        else if(userRequest.getClientRegistration().getRegistrationId().equals("naver")) {
            oAuth2UserInfo = new NaverUserInfo((Map)oAuth2User.getAttributes().get("response"));
        }
        else if(userRequest.getClientRegistration().getRegistrationId().equals("kakao")) {
            oAuth2UserInfo = new KakaoUserInfo((Map)oAuth2User.getAttributes().get("kakao_account"),
                    String.valueOf(oAuth2User.getAttributes().get("id")));
        }
        else{
            log.info("지원하지 않은 로그인 서비스 입니다.");
        }

        String email = "SocialEmail" + oAuth2UserInfo.getEmail();
        String provider = oAuth2UserInfo.getProvider(); //google , naver, facebook etc
        log.info("email :" + email);

        // 이전에 가입한 이메일이 있는지 DB와 비교, 가입한 적 있는 경우
        Member member = memberRepository.findByEmail(email).orElse(null);
        // 가입한 적 없는 경우,
        if (member == null) {
            member = saveUser(oAuth2UserInfo);
            log.info("MEMber : null");
        }
        log.info("MEMber : null 아님"+ member.toString());

//            가입한 적 있는 경우 = (같은 이메일 존재)
//            if (!member.getProvider().equals(provider)) { // 소셜로그인에서 같은 소셜 로그인으로 이미 가입된 경우,
//                throw new RuntimeException("Email already signed up.");
//            }
////            다른 소셜 로그인일 때 -> 그냥 처리안해도 될 듯.
////            member = updateUser(member, oAuth2UserInfo);

        return new Details(member, oAuth2User.getAttributes());

    }

    private Member saveUser(OAuth2UserInfo oAuth2UserInfo){
        LocalDateTime regdate = LocalDateTime.now();
        String provider = oAuth2UserInfo.getProvider(); //google , naver, facebook etc
        String providerId = oAuth2UserInfo.getProviderId();
        String name = provider + "_" + providerId;
        Role role = Role.USER;

        Member member = Member.builder()
                .name(name)
                .email("SocialEmail" + oAuth2UserInfo.getEmail())
                .role(role)
                .provider(provider)
                .provideId(providerId)
                .regdate(regdate)
                .build();

        memberRepository.save(member);
        return member;
    }

    //    private Member updateUser(Member member, OAuth2UserInfo oAuth2UserInfo) {
//        return memberRepository.save(member.update(oAuth2UserInfo));
//    }


}