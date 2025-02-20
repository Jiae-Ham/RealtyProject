package com.Realty.RealtyWeb.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Purpose {
    주거용("주거용"),
    상업용("상업용"),
    오피스("오피스"),
    기타 ("기타");

    private final String value;

    Purpose(String value) {
        this.value = value;
    }

    @JsonValue // Json 변환 시 한글 그대로 사용
    public String getValue() {
        return value;
    }

    @JsonCreator // Json 요청에서 한글을 Enum으로 변환
    public static Purpose fromValue(String value) {
        for (Purpose purpose : Purpose.values()) {
            if (purpose.value.equalsIgnoreCase(value)) {
                return purpose;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }

}


