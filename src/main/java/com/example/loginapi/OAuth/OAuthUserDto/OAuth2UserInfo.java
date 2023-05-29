package com.example.loginapi.OAuth.OAuthUserDto;

public interface OAuth2UserInfo {

    //소셜 식별 값 : 구글 - "sub", 카카오 - "id", 네이버 - "id"
    String getProviderId();
    String getProvider();
    String getEmail();
    String getName();

}

