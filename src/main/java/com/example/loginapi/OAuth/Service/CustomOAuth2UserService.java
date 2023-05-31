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
        log.info(oAuth2User.toString());
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
            oAuth2UserInfo = new KakaoUserInfo((Map)oAuth2User.getAttributes().get("properties"),
                    String.valueOf(oAuth2User.getAttributes().get("id")));
        }
        else{
            log.info("지원하지 않은 로그인 서비스 입니다.");
        }

        // 소셜 로그인은 특별히 앞에 SocialEmail 추가
        String email = "SocialEmail" + oAuth2UserInfo.getEmail();
        log.info("email :" + email);
        log.info("oAuth2User : "+ oAuth2User.toString());
        log.info("oAuth2Userattr : "+ oAuth2User.getAttributes().toString());


        // 이전에 가입한 이메일이 있는지 DB와 비교, 가입한 적 있는 경우
        Member member = memberRepository.findByEmail(email).orElse(null);
        // 가입한 적 없는 경우,
        if (member == null) {
            member = saveUser(oAuth2UserInfo, email);
            log.info("MEMber : null");
        } else {
            log.info("MEMber : null아님");
        }
        log.info("MEMber : "+ member.toString());


//            가입한 적 있는 경우 = (같은 이메일 존재)
//            if (!member.getProvider().equals(provider)) { // 소셜로그인에서 같은 소셜 로그인으로 이미 가입된 경우,
//                throw new RuntimeException("Email already signed up.");
//            }
////            다른 소셜 로그인일 때 -> 그냥 처리안해도 될 듯.
////            member = updateUser(member, oAuth2UserInfo);

        return new Details(member, oAuth2User.getAttributes());

    }

    private Member saveUser(OAuth2UserInfo oAuth2UserInfo, String email){
        // provider
        String provider = oAuth2UserInfo.getProvider(); //google , naver, facebook etc
        // name
        String name = provider + "_" + oAuth2UserInfo.getName();
        // email 은 매개변수에
        // providerId
        String providerId = oAuth2UserInfo.getProviderId();
        // 이미지 url
        String picUrl = oAuth2UserInfo.getImageUrl();
        // Role, regdate
        Role role = Role.USER;
        LocalDateTime regdate = LocalDateTime.now();

        Member member = Member.builder()
                .provider(provider)
                .name(name)
                .email(email)
                .provideId(providerId)
                .pic(picUrl)
                .role(role)
                .regdate(regdate)
                .build();

        memberRepository.save(member);
        return member;
    }

    //    private Member updateUser(Member member, OAuth2UserInfo oAuth2UserInfo) {
//        return memberRepository.save(member.update(oAuth2UserInfo));
//    }


}