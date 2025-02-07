package com.Realty.RealtyWeb.controller;

import com.Realty.RealtyWeb.token.JwtToken;
import com.Realty.RealtyWeb.token.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import com.Realty.RealtyWeb.services.MemberService;
import com.Realty.RealtyWeb.dto.MemberDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    //회원가입
    @PostMapping("/join")
    public ResponseEntity<MemberDTO> join(@RequestBody MemberDTO memberDTO) {
        return ResponseEntity.ok(memberService.join(memberDTO));
    }

    //id 중복체크
    @PostMapping("/checkId/{userId}")
    public ResponseEntity<Boolean> checkId(@PathVariable String userId) {
        return ResponseEntity.ok(!memberService.checkId(userId));
    }

    //로그인
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> loginInfo) {
        String userId = loginInfo.get("userId");
        String userPw = loginInfo.get("userPw");

        JwtToken token = memberService.login(userId, userPw);
        if (token != null) { // 로그인 성공 시
            Map<String, String> response = new HashMap<>();
            response.put("accessToken", token.getAccessToken());
            response.put("refreshToken", token.getRefreshToken());
            return ResponseEntity.ok(response);
        } else { // 로그인 실패 시
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "invalid credentials"));
        }
    }


    //Refresh Token을 이용한 액세스 토큰 재발급
    @PostMapping("/token/refresh")
    public ResponseEntity<Map<String, String>> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        JwtToken newToken = jwtTokenProvider.refreshToken(refreshToken);

        if (newToken != null) {
            Map<String, String> response = new HashMap<>();
            response.put("accessToken", newToken.getAccessToken());
            response.put("refreshToken", newToken.getRefreshToken());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "invalid credentials"));
        }

    }

    //로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token format");
        }

        // 공백을 제거하고 실제 토큰만 추출
        String token = authorizationHeader.replace("Bearer ", "").trim();

        try {
            String userId = jwtTokenProvider.getAuthentication(token).getName();
            jwtTokenProvider.revokeRefreshToken(userId);
            return ResponseEntity.ok("로그아웃 완료");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
    }


    //비밀번호 찾기 페이지 이동
    @PostMapping("/find-password")
    public ResponseEntity<String> findPassword(@RequestBody Map<String, String> request) {
        String userName = request.get("userName");
        String userId = request.get("userId");
        String userPhone = request.get("userPhone");

        MemberDTO member = memberService.findByUser(userName, userId, userPhone);
        if (member == null) {
            return ResponseEntity.ok("회원님의 비밀번호는 " + member.getUserPw() + " 입니다.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("회원 정보를 찾을 수 없습니다.");

    }
    
    //비밀번호 변경
    @PutMapping("/update-password")
    public ResponseEntity<String> updatePassword(@RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");

        boolean success = memberService.updatePassword(userId, oldPassword, newPassword);
        if (success) {
            return ResponseEntity.ok("비밀번호 변경 향상됩니다.");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("기존 비밀번호가 일치하지 않습니다.");
    }

    //마이페이지 이동
    @GetMapping("/my-page/{userId}")
    public ResponseEntity<MemberDTO> myPage(@PathVariable String userId) {
        return memberService.findByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    //회원 리스트 이동
    @GetMapping("/list")
    public ResponseEntity<List<MemberDTO>> getAllMembers() {
        return ResponseEntity.ok(memberService.findByAllMember());
    }

    //회원정보 수정
    @PutMapping("/update")
    public ResponseEntity<MemberDTO> update(@RequestBody MemberDTO memberDTO) {
        return ResponseEntity.ok(memberService.update(memberDTO));
    }

    //회원정보 탈퇴
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<Boolean> delete(@PathVariable String userId, @RequestParam String userPw) {
        try {
            boolean success = memberService.delete(userId, userPw);
            return ResponseEntity.ok(success);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false); // 비밀번호 불일치
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false); // 사용자 없음
        }
    }

}
