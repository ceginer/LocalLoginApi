package com.example.loginapi.jwt.provider;

import com.example.loginapi.jwt.UserDetailsToken.Details;
import com.example.loginapi.jwt.UserDetailsToken.DetailsService;
import com.example.loginapi.jwt.util.JwtTokenizer;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Date;

// Provider 의 역할 : JwtToken 의 검증
// -> createToken(만들기, Access/refresh),
// -> parseToken(파싱, Access/refresh) ,
// -> 유저 ID 로부터 Token 얻기 (파싱하여 DB이용) ,

@Component
public class JwtAuthenticationProvider{

//    private final JwtTokenizer jwtTokenizer;

    @Value("${jwt.secretKey}")
    private byte[] accessSecret;

    @Value("{jwt.refreshKey}")
    private byte[] refreshSecret;

    public final static Long ACCESS_TOKEN_EXPIRE_COUNT = 30 * 60 * 1000L; // 30 minutes
    public final static Long REFRESH_TOKEN_EXPIRE_COUNT = 7 * 24 * 60 * 60 * 1000L; // 7 days

    @Autowired
    private DetailsService userDetailsService;

    /**
     * 적절한 설정을 통해 토큰을 생성하여 반환
     * @param authentication, byte[], Long
     * @return String
     */

    public String createToken(Authentication authentication, byte[] secret_key, Long expire_time) {

        Claims claims = Jwts.claims().setSubject(authentication.getName()); // getName으로 sub에 이메일 넣기

        Date now = new Date();
        Date expiresIn = new Date(now.getTime() + expire_time);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiresIn)
                .signWith(SignatureAlgorithm.HS256, secret_key)
                .compact();
    }

    public String createAccessToken(Authentication authentication){

        return createToken(authentication,accessSecret, ACCESS_TOKEN_EXPIRE_COUNT);
    }

    public String createRefreshToken(Authentication authentication){
        return createToken(authentication, refreshSecret, REFRESH_TOKEN_EXPIRE_COUNT);
    }



    /**
     * 토큰이 서버에서 발행했는지, 만료시간이 되었는지를 검증 (create 할 때 만료시간 만들기)
     * @param (TokenString, secretkey)
     * @return Claims
     */
    public Claims validateToken(String TokenString, byte[] secretkey) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretkey)
                //서버의 Secretkey와 대조, 실패하면 SignatureException 발생
                .build()
                .parseClaimsJws(TokenString)// Jws 객체형태로 돌려줌 + 만료 검사 수행
                // 만료일자 지나면 JwtException 발생
                .getBody(); // Claims 객체 반환

        // 여기까지 진행되면 아무 문제 없는 것이므로, return true;
        return claims;
    }


    public Claims validateAccessToken(String accessTokenString){
        return validateToken(accessTokenString, accessSecret);
    }
    public Claims validateRefreshToken(String refreshTokenString){
        return validateToken(refreshTokenString, refreshSecret);
    }

    public UsernamePasswordAuthenticationToken getToken(Claims claims){
        String email = claims.getSubject();
        String role = claims.get("role", String.class);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        UsernamePasswordAuthenticationToken token
                = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        return token;
    }

}
