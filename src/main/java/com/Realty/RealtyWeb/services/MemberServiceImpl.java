package com.Realty.RealtyWeb.services;

import com.Realty.RealtyWeb.Entity.UserEntity;
import com.Realty.RealtyWeb.dto.MemberDTO;
import com.Realty.RealtyWeb.repository.MemberRepository;
import com.Realty.RealtyWeb.repository.UserRepository;
import com.Realty.RealtyWeb.token.JwtToken;
import com.Realty.RealtyWeb.token.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @Override
    public boolean checkId(String userId) {
        return memberRepository.existsById(userId);
    }

    //회원가입
    @Override
    public MemberDTO join(MemberDTO memberDTO) {
        if (checkId(memberDTO.getUserId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }else if (checkName(memberDTO.getUserName())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        //비밀번호 암호화 후 저장
        memberDTO.setUserPw(passwordEncoder.encode(memberDTO.getUserPw()));
        return memberRepository.save(memberDTO);
    }

    //로그인
   @Override
    public JwtToken login(String userId, String userPw) {
        //DB에서 사용자 정보 조회
       UserEntity user = userRepository.findByUserId(userId);
       if (user == null) {
           throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
       }

       //비밀번호 검증
       if (!passwordEncoder.matches(userPw, user.getUserPw())) {
           throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
       }

       //Spring Security 인증 객체 생성
       Authentication authentication =
               new UsernamePasswordAuthenticationToken(userId, userPw, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

       return jwtTokenProvider.generateToken(authentication);
   }

   //특정 회원 조회
   @Override
   public Optional<MemberDTO> findByUserId(String userId) {
       return memberRepository.findByUserId(userId);
   }

    //모든 회원 조회
    @Override
    public List<MemberDTO> findByAllMember() {
        return memberRepository.findAll();
    }


    //회원 삭제
    @Override
    public boolean delete(String userId, String userPw) {
        return memberRepository.findByUserId(userId).map(member -> {
            // 입력된 비밀번호가 DB 비밀번호와 일치하는지 검증
            if (!passwordEncoder.matches(userPw, member.getUserPw())) {
                throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
            }

            // 회원 탈퇴 (데이터 삭제)
            memberRepository.delete(member);
            return true;
        }).orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }


    // 이메일 중복 체크 - 필요한가?
    @Override
    public boolean checkEmail(String userEmail) {
        return memberRepository.findByUserEmail(userEmail).isPresent();
    }

    //사용자 이름 중복 체크 - 이것도 필요한가?
    @Override
    public boolean checkName(String userName) {
        return memberRepository.findByUserName(userName).isPresent();
    }

    //회원정보 수정
    @Override
    public MemberDTO update(MemberDTO memberDTO) {
        return memberRepository.findByUserId(memberDTO.getUserId()).map(member -> {
            member.setUserPw(passwordEncoder.encode(memberDTO.getUserPw()));
            member.setUserName(memberDTO.getUserName());
            member.setUserEmail(memberDTO.getUserEmail());
            member.setUserPhone(memberDTO.getUserPhone());
            member.setUserImg(memberDTO.getUserImg());
            return memberRepository.save(member);
        }).orElseThrow(() -> new IllegalArgumentException("이미 존재하지 않는 회원입니다."));
    }

    //비밀번호 변경
    @Override
    public boolean updatePassword(String userId, String oldPassword, String newPassword) {
        return memberRepository.findByUserId(userId).map(member -> {
            // 기존 비밀번호 검증
            if (!passwordEncoder.matches(oldPassword, member.getUserPw())) {
                throw new BadCredentialsException("기존 비밀번호가 일치하지 않습니다.");
            }

            // 새 비밀번호 암호화 후 저장
            member.setUserPw(passwordEncoder.encode(newPassword));
            memberRepository.save(member);
            return true;
        }).orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }

    // 비밀번호 찾기
    @Override
    public MemberDTO findByUser(String username, String userid, String userphone) {
        return memberRepository.findByUserNameAndUserIdAndUserPhone(username, userid, userphone)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

}
