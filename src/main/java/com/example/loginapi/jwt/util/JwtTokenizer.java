//package com.example.loginapi.jwt.util;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import java.nio.charset.StandardCharsets;
//
//@Component
//public class JwtTokenizer {
//
//    private final byte[] accessSecret;
//    private final byte[] refreshSecret;
//
//    public final static Long ACCESS_TOKEN_EXPIRE_COUNT = 30 * 60 * 1000L; // 30 minutes
//    public final static Long REFRESH_TOKEN_EXPIRE_COUNT = 7 * 24 * 60 * 60 * 1000L; // 7 days
//
//    public JwtTokenizer(@Value("${jwt.secretKey}") String accessSecret, @Value("${jwt.secret}") String refreshSecret){
//        this.accessSecret = accessSecret.getBytes(StandardCharsets.UTF_8);
//        this.refreshSecret = refreshSecret.getBytes(StandardCharsets.UTF_8);
//    }
//
//    public String createToken(){
//
//    }
//
//
//
//
//}
