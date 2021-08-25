package de.volkswagen.springevchargingflagsapi.model;


import com.fasterxml.jackson.annotation.JsonProperty;
public class FlagDto {
    @JsonProperty("locationid")
    private int locationId;
    private StatusEnum status;
    private Integer id = null;
    private Float ccs2 = null;
    private Float type2 = null;
    private Float chademo = null;
    private Float tesla = null;

    public FlagDto() {

    }

    public FlagDto(int locationId, StatusEnum status, Float ccs2, Float type2, Float chademo, Float tesla) {
        this.locationId = locationId;
        this.status = status;
        this.ccs2 = ccs2;
        this.type2 = type2;
        this.chademo = chademo;
        this.tesla = tesla;
    }

    public FlagDto(int locationId, StatusEnum status, Integer id, Float ccs2, Float type2, Float chademo, Float tesla) {
        this.locationId = locationId;
        this.status = status;
        this.id = id;
        this.ccs2 = ccs2;
        this.type2 = type2;
        this.chademo = chademo;
        this.tesla = tesla;
    }

    public int getLocationId() {
        return locationId;
    }

    public StatusEnum getStatus() {
        return status;
    }

    public Integer getId() {
        return id;
    }

    public Float getCcs2() {
        return ccs2;
    }

    public Float getType2() {
        return type2;
    }

    public Float getChademo() {
        return chademo;
    }

    public Float getTesla() {
        return tesla;
    }
}
