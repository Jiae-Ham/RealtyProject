package com.Realty.RealtyWeb.dto;

import com.Realty.RealtyWeb.Entity.RegisterAnalysisEntity;
import com.Realty.RealtyWeb.enums.Purpose;
import com.Realty.RealtyWeb.enums.RiskLevel;
import com.Realty.RealtyWeb.enums.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class RegisterAnalysisDTO {
    private Long pid;
    private String userid;

    private String owner;
    private LocalDateTime issueDate;

    private String riskLevel;
    private String riskKeywords;
    private String mainWarnings;

    private BigDecimal maxClaim;
    private BigDecimal protectedAmount;

    private Purpose purpose;
    private TransactionType transactionType;
    private BigDecimal price;
    private BigDecimal exclusiveArea;

    private String pdfBase64;

    private List<RiskDetail> riskDetails;

    // Entity -> DTO 매핑 정적 메서드

    public static RegisterAnalysisEntity toEntity(RegisterAnalysisDTO dto) {
        return RegisterAnalysisEntity.builder()
                .userid(dto.getUserid())
                .pid(dto.getPid())
                .owner(dto.getOwner())
                .issue_date(dto.getIssueDate())
                .risk_level(RiskLevel.valueOf(dto.getRiskLevel()))
                .risk_keywords(dto.getRiskKeywords())
                .main_warnings(dto.getMainWarnings())
                .max_claim(dto.getMaxClaim())
                .protected_amount(dto.getProtectedAmount())
                .purpose(Purpose.valueOf(dto.getPurpose().toString()))
                .transactionType(TransactionType.valueOf(dto.getTransactionType().toString()))
                .price(dto.getPrice())
                .exclusiveArea(dto.getExclusiveArea())
                .pdf_base64(dto.getPdfBase64())
                .build();
    }

    public static RegisterAnalysisDTO fromEntity(RegisterAnalysisEntity entity) {
        return RegisterAnalysisDTO.builder()
                .userid(entity.getUserid())
                .pid(entity.getPid())
                .owner(entity.getOwner())
                .issueDate(entity.getIssue_date())
                .riskLevel(entity.getRisk_level().toString())
                .riskKeywords(entity.getRisk_keywords())
                .mainWarnings(entity.getMain_warnings())
                .maxClaim(entity.getMax_claim())
                .protectedAmount(entity.getProtected_amount())
                .purpose(Purpose.valueOf(entity.getPurpose().toString()))
                .transactionType(TransactionType.valueOf(entity.getTransactionType().toString()))
                .price(entity.getPrice())
                .exclusiveArea(entity.getExclusiveArea())
                .pdfBase64(entity.getPdf_base64())
                .build();
    }
}
