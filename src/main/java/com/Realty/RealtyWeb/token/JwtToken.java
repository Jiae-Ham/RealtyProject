package com.Realty.RealtyWeb.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class JwtToken {

    //Jwt인증 타입을 나타내는 문자열 필드
    private String grantToken;

    // 접근 토큰을 나타내는 문자열 필드
    private String accessToken;

    //갱신 토큰
    private String refreshToken;
}
