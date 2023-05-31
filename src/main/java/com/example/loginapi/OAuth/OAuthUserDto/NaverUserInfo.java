package com.example.loginapi.OAuth.OAuthUserDto;

import java.util.Map;

public class NaverUserInfo implements OAuth2UserInfo {
    private Map<String, Object> attributes;

    public NaverUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getEmail() {
        return String.valueOf(attributes.get("email"));
    }

    @Override
    public String getName() {
        return String.valueOf(attributes.get("name"));
    }

//    @Override
//    public String getImageUrl() {
//        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
//
//        if (response == null) {
//            return null;
//        }
//
//        return (String) response.get("profile_image");
//    }
}
