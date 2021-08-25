package de.volkswagen.springevchargingapi;

import de.volkswagen.springevchargingapi.model.Location;
import de.volkswagen.springevchargingapi.model.LocationList;
import de.volkswagen.springevchargingapi.model.UpdateOwnerRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/location")
public class LocationController {
    LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping("/stations")
    @ResponseStatus(HttpStatus.OK)
    public LocationList getLocations() {
        return this.locationService.getLocations();
    }

    @GetMapping("/station/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Location getLocation(@PathVariable int id) {
        return this.locationService.getLocation(id);
    }

    @PostMapping("/stations")
    @ResponseStatus(HttpStatus.OK)
    public Location postLocation(@RequestBody  Location location) {
        return this.locationService.postLocation(location);
    }

    @PatchMapping("/station/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Location patchLocation(@PathVariable int id,
                                  @RequestBody UpdateOwnerRequest updateOwnerRequest) {
        Location loc = this.locationService.patchLocation(id, updateOwnerRequest.getOwner());
        loc.setOwner(updateOwnerRequest.getOwner());
        return loc;
    }

    @DeleteMapping("/station/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteLocation(@PathVariable int id) {
        this.locationService.deleteLocation(id);
    }
}
