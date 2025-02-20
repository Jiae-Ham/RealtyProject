package com.Realty.RealtyWeb.repository;

import com.Realty.RealtyWeb.Entity.HouseBoardEntity;
import com.Realty.RealtyWeb.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HouseBoardRepository extends JpaRepository<HouseBoardEntity, Long> {

    // 특정 유저가 작성한 게시글 조회
    List<HouseBoardEntity> findByWriter(UserEntity userEntity);

}
