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
        return null;
    }

//    @Override
//    public String getImageUrl() {
//        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
//
//        if (properties == null) {
//            return null;
//        }
//
//        return (String) properties.get("thumbnail_image");
//    }
}
