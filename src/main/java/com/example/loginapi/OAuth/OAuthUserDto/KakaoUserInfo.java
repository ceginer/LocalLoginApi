package com.example.loginapi.OAuth.OAuthUserDto;

import java.util.Map;

public class KakaoUserInfo implements OAuth2UserInfo {

    private String id;
    private Map<String, Object> kakaoAccount;

    public KakaoUserInfo(Map<String, Object> attributes, String id) {
        this.kakaoAccount = attributes;
        this.id = id;
    }

    @Override
    public String getProviderId() {
        return id;
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getEmail() {
        return String.valueOf(kakaoAccount.get("email"));
    }

    @Override
    public String getName() {
        return String.valueOf(kakaoAccount.get("nickname"));
    }

    @Override
    public String getImageUrl() {
//        Map<String, Object> properties = (Map<String, Object>) kakaoAccount.get("properties");
//
//        if (properties == null) {
//            return null;
//        }
//
//        return (String) properties.get("thumbnail_image");
        return String.valueOf(kakaoAccount.get("profile_image"));
    }
}
