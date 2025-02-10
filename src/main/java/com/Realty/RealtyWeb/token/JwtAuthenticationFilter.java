package com.Realty.RealtyWeb.token;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider; //Jwt 토큰을 생성하고 유저 정보를 저장하고 토큰를 전송할 수 있습니다
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Request Header에서 Jwt 토큰 추출
        String token = resolveToken(request);

        // 토큰 유효성 검사 후 인증 처리
        if (token != null && jwtTokenProvider.validateToken(token)) {
            // 토큰이 유효할 경우 인증 객체 생성
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            // SecurityContext에 인증 객체 설정 -> 요청을 처리하는 동안 인증 정보가 유지됨
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        // Request를 filterChain로 전송 -> 다음 필터로 이동
        filterChain.doFilter(request, response);


    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer "를 부분 제거하고 토큰 반환
        }
        return null;
    }

}
