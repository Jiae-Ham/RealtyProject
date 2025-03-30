package com.Realty.RealtyWeb.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum TransactionType {
    매매("매매"),
    전세("전세"),
    월세 ("월세");

    private final String value;

    TransactionType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static TransactionType fromValue(String value) {
        for (TransactionType transactionType : TransactionType.values()) {
            if (transactionType.getValue().equalsIgnoreCase(value)) {
                return transactionType;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }
}

