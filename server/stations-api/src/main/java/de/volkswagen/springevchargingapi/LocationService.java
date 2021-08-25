package de.volkswagen.springevchargingapi;

import de.volkswagen.springevchargingapi.model.Location;
import de.volkswagen.springevchargingapi.model.LocationList;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class LocationService {
    private final LocationRepository locationRepository;

    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public LocationList getLocations() {
        List<Location> locationList = this.locationRepository.findAll();
        if (locationList.isEmpty()) {
            throw new LocationNotFoundException();
        }
        return new LocationList(locationList);
    }

    public Location getLocation(int locationId) {
        return this.locationRepository.findById(locationId).orElseThrow(LocationNotFoundException::new);
    }

    public Location postLocation(Location location) {
        if(Objects.isNull(location.getOwner())) {
            throw new InvalidParameterException();
        }
        return this.locationRepository.save(location);
    }

    public Location patchLocation(int id, String owner) {
        Location location = this.locationRepository.findById(id).orElseThrow(LocationNotFoundException::new);
        location.setOwner(owner);
        return this.locationRepository.save(location);
    }

    public void deleteLocation(int id) {
        Location location = this.locationRepository.findById(id).orElseThrow(LocationNotFoundException::new);
        this.locationRepository.delete(location);
    }
}
