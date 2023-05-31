package com.example.loginapi.OAuth.OAuthUserDto;

import java.util.Map;

public class FacebookUserInfo implements OAuth2UserInfo {
    private Map<String, Object> attributes;

    public FacebookUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getProvider() {
        return "facebook";
    }

    @Override
    public String getEmail() {
        return String.valueOf(attributes.get("email"));
    }

    @Override
    public String getName() {
        return String.valueOf(attributes.get("name"));
    }
    @Override
    public String getImageUrl() {
        return (String) attributes.get("imageUrl");
    }
}
