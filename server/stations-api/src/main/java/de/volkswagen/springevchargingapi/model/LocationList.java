package de.volkswagen.springevchargingapi.model;

import java.util.ArrayList;
import java.util.List;

public class LocationList {
    private List<Location> locationList;

    public LocationList() {
        this.locationList = new ArrayList<>();
    }

    public LocationList(List<Location> locationList) {
        this.locationList = locationList;
    }

    public List<Location> getLocations() {
        return this.locationList;
    }
}
