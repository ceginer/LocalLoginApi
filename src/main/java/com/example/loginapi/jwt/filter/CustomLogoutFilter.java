//package com.example.loginapi.jwt.filter;
//
//import com.example.loginapi.jwt.UserDetailsToken.Details;
//import com.example.loginapi.service.MemberService;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.ServletRequest;
//import jakarta.servlet.ServletResponse;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.logout.LogoutFilter;
//import org.springframework.security.web.authentication.logout.LogoutHandler;
//import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
//
//import java.io.IOException;
//
//@Slf4j
//public class CustomLogoutFilter extends LogoutFilter {
//
//    private final MemberService memberService;
//
//    public CustomLogoutFilter(LogoutSuccessHandler logoutSuccessHandler, MemberService memberService, LogoutHandler... handlers) {
//        super(logoutSuccessHandler, handlers);
//        this.memberService = memberService;
//    }
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//        super.doFilter(request, response, chain);
//
//        SecurityContext securityContext = SecurityContextHolder.getContext();
//        log.info(securityContext.toString());
//
//        // 인증객체로부터 Id 추출
//        Authentication authentication = securityContext.getAuthentication();
//        if(authentication != null){
//            Details userDetails = (Details) authentication.getPrincipal();
//            String memberId = String.valueOf(userDetails.getMember().getMemberId());
//
//            // Id와 일치하는 DB(redis)에 존재하는 key 삭제
//            memberService.checkAndDeleteRefresh(memberId); // 찾지 못할 시, RuntimeException 발생
//            memberService.expireCookie((HttpServletResponse) response,"RefreshToken"); // 클라이언트에 만료된 쿠키 전달 -> 쿠키삭제
//
//            Long expiration = jwtAuthenticationProvider.getExpiration(logout.getAccessToken());
//        }
//
//
//
//        // 인증객체 꾸러미 SecurityContextHolder 를 clear()
//        // 사실 웹 어플리케이션에서는 stateless 로 이루어지기 때문에 굳이 안해도 되는데 특수 상황에선 써야한다고 함.
//        SecurityContextHolder.clearContext();
//        log.info(securityContext.toString());
//
//        return new ResponseEntity("Logout Success", HttpStatus.OK);
//    }
//}
