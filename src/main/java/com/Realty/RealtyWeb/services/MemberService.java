package com.Realty.RealtyWeb.services;

import com.Realty.RealtyWeb.dto.MemberDTO;
import com.Realty.RealtyWeb.token.JwtToken;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public interface MemberService {

    boolean checkId(String userId); // id 중복 체크

    MemberDTO join(MemberDTO memberDTO); // 회원가입

    JwtToken login(String userId, String userPw); //로그인

    Optional<MemberDTO> findByUserId(String userId); // 특정 회원 조회


    //모든회원 조회
    List<MemberDTO> findByAllMember();

    //회원 탈퇴
    boolean delete(String userId, String userPw);

    //이메일 중복 체크
    boolean checkEmail(String userEmail);

    //사용자 이름 중복 체크
    boolean checkName(String userName);

    //회원정보 수정
    MemberDTO update(MemberDTO memberDTO);

    boolean updatePassword(String userId, String oldPassword, String newPassword);

    //비밀번호 찾기
    MemberDTO findByUser(String username, String userid, String userphone);

}
