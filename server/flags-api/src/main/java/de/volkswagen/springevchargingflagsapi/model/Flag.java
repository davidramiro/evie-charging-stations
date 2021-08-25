package de.volkswagen.springevchargingflagsapi.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.*;
import java.util.Objects;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Flag {
    @Column(name = "flag_id")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "locationid")
    @JsonProperty("locationid")
    private Integer locationId = null;

    @JsonProperty("status")
    private StatusEnum status = null;

    @JsonProperty("ccs2")
    private Float ccs2 = null;

    @JsonProperty("type2")
    private Float type2 = null;

    @JsonProperty("chademo")
    private Float chademo = null;

    @JsonProperty("tesla")
    private Float tesla = null;

    public Flag id(Integer id) {
        this.id = id;
        return this;
    }

    public Flag(Integer locationId, StatusEnum status, Float ccs2, Float type2, Float chademo, Float tesla) {
        this.locationId = locationId;
        this.status = status;
        this.ccs2 = ccs2;
        this.type2 = type2;
        this.chademo = chademo;
        this.tesla = tesla;
    }

    public Flag(int id, Integer locationId, StatusEnum status, Float ccs2, Float type2, Float chademo, Float tesla) {
        this.id = id;
        this.locationId = locationId;
        this.status = status;
        this.ccs2 = ccs2;
        this.type2 = type2;
        this.chademo = chademo;
        this.tesla = tesla;
    }

    public Flag() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    public Float getCcs2() {
        return ccs2;
    }

    public void setCcs2(Float ccs2) {
        this.ccs2 = ccs2;
    }

    public Float getType2() {
        return type2;
    }

    public void setType2(Float type2) {
        this.type2 = type2;
    }

    public Float getChademo() {
        return chademo;
    }

    public void setChademo(Float chademo) {
        this.chademo = chademo;
    }

    public Float getTesla() {
        return tesla;
    }

    public void setTesla(Float tesla) {
        this.tesla = tesla;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Flag flag = (Flag) o;
        return Objects.equals(this.id, flag.id) &&
                Objects.equals(this.locationId, flag.locationId) &&
                Objects.equals(this.status, flag.status) &&
                Objects.equals(this.ccs2, flag.ccs2) &&
                Objects.equals(this.type2, flag.type2) &&
                Objects.equals(this.chademo, flag.chademo) &&
                Objects.equals(this.tesla, flag.tesla);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, locationId, status, ccs2, type2, chademo, tesla);
    }

    @Override
    public String toString() {
        return "Flag{" +
                "id=" + id +
                ", locationId=" + locationId +
                ", status=" + status +
                ", ccs2=" + ccs2 +
                ", type2=" + type2 +
                ", chademo=" + chademo +
                ", tesla=" + tesla +
                '}';
    }
}
