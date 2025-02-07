package com.Realty.RealtyWeb.repository;

import com.Realty.RealtyWeb.dto.MemberDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<MemberDTO, String> {
    Optional<MemberDTO> findByUserId(String userId);
    Optional<MemberDTO> findByUserEmail(String userEmail);

    Optional<MemberDTO> findByUserName(String userName);

    Optional<MemberDTO> findByUserNameAndUserIdAndUserPhone(String username, String userid, String userphone);
}
