package de.volkswagen.springevchargingapi.model;


import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;
import java.util.Objects;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Location {
    @Column(name = "location_id")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String owner;
    private double latitude;
    private double longitude;

    public Location(String owner, double latitude, double longitude) {
        this.owner = owner;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Location(int id, String owner, double latitude, double longitude) {
        this.id = id;
        this.owner = owner;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Location() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Double.compare(location.latitude, latitude) == 0 && Double.compare(location.longitude, longitude) == 0 && Objects.equals(owner, location.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, latitude, longitude);
    }
}
