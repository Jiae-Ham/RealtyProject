package com.Realty.RealtyWeb.dto;

import com.Realty.RealtyWeb.Entity.HouseInfoEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseInfoDTO {
    private Long houseId;        // 매물 ID
    private Long pid;            // 게시글 ID
    private String buildingName; // 건물 이름
    private String purpose;      // 용도
    private String transactionType; // 거래 유형
    private Double price;        // 가격
    private Double maintenanceFee; // 관리비
    private String address;      // 주소
    private String addressDetail;// 상세 주소
    private Double exclusiveArea;// 전용 면적
    private Double supplyArea;   // 공급 면적
    private Integer rooms;       // 방 개수
    private Integer bathrooms;   // 욕실 개수
    private Integer floor;       // 층수
    private String direction;    // 방향
    private String builtYear;    // 준공 연도
    private String loanAvailable;// 대출 가능 여부
    private String pet;          // 반려동물 가능 여부
    private String parking;      // 주차 가능 여부
    private String houseDetail;  // 상세 설명

    public static HouseInfoDTO fromEntity(HouseInfoEntity houseInfoEntity) {
        return HouseInfoDTO.builder()
                .houseId(houseInfoEntity.getHouseId())
                .pid(houseInfoEntity.getHouseBoardEntity().getPid())
                .buildingName(houseInfoEntity.getBuildingName())
                .purpose(houseInfoEntity.getPurpose().name())
                .transactionType(houseInfoEntity.getTransactionType().name())
                .price(houseInfoEntity.getPrice())
                .maintenanceFee(houseInfoEntity.getMaintenanceFee())
                .address(houseInfoEntity.getAddress())
                .addressDetail(houseInfoEntity.getAddressDetail())
                .exclusiveArea(houseInfoEntity.getExclusiveArea())
                .supplyArea(houseInfoEntity.getSupplyArea())
                .rooms(houseInfoEntity.getRooms())
                .bathrooms(houseInfoEntity.getBathrooms())
                .floor(houseInfoEntity.getFloor())
                .direction(houseInfoEntity.getDirection().name())
                .builtYear(houseInfoEntity.getBuiltYear())
                .loanAvailable(houseInfoEntity.getLoanAvailable().name())
                .pet(houseInfoEntity.getPet().name())
                .parking(houseInfoEntity.getParking().name())
                .houseDetail(houseInfoEntity.getHouseDetail())
                .build();
    }
}
