package de.volkswagen.springevchargingflagsapi.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum StatusEnum {
    AVAILABLE("available"),

    IN_USE("in-use"),

    MAINTENANCE("maintenance"),

    DEFECT("defect");

    private String value;

    StatusEnum() {
    }

    StatusEnum(String value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static StatusEnum fromValue(String text) {
        for (StatusEnum b : StatusEnum.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }
}
