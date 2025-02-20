package com.Realty.RealtyWeb.dto;

import com.Realty.RealtyWeb.Entity.HouseBoardEntity;
import com.Realty.RealtyWeb.Entity.HouseInfoEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HouseBoardSummaryDTO {

    private Long pid;                 // 게시글 ID
    private String ptitle;            // 제목
    private String houseType;         // 주택 유형
    private String transactionType;   // 거래 유형
    private Double price;            // 거래 금액
    private Double exclusiveArea;    // 전용면적
    private Integer floor;            // 층
    private String address;           // 주소 (위치)
    private Integer views;            // 조회수
    private String writerName;        // 작성자 닉네임

    public static HouseBoardSummaryDTO fromEntity(HouseBoardEntity board, HouseInfoEntity info) {
        return HouseBoardSummaryDTO.builder()
                .pid(board.getPid())
                .ptitle(board.getPtitle())
                .houseType(info.getPurpose().name())  // 주택 유형
                .transactionType(info.getTransactionType().name()) // 거래 유형
                .price(info.getPrice())  // 가격
                .exclusiveArea(info.getExclusiveArea())  // 전용면적
                .floor(info.getFloor())  // 층
                .address(info.getAddress())  // 위치
                .views(board.getViews())  // 조회수
                .writerName(board.getWriter().getDisplayName())  // 작성자 닉네임
                .build();
    }

}
