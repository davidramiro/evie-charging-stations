package de.volkswagen.springevchargingapi;

import de.volkswagen.springevchargingapi.model.Location;
import de.volkswagen.springevchargingapi.model.LocationList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class LocationServiceTests {
    private LocationService locationService;
    @Mock
    LocationRepository locationRepository;


    private List<Location> locationList;
    private final int LOCATION_COUNT = 100;


    @BeforeEach
    void setUp() {
        this.locationService = new LocationService(this.locationRepository);
        this.locationList = new ArrayList<>();
        for(int i=1; i <= LOCATION_COUNT; i++) {
            Location location = new Location(i, "testOwner" + i, LOCATION_COUNT-i, i);
            this.locationList.add(location);
        }
    }

    @Test
    void getLocations_noParams_exists_returnLocationList() {
        when(this.locationRepository.findAll()).thenReturn(this.locationList);
        LocationList locationList = this.locationService.getLocations();
        assertThat(locationList).isNotNull();
        assertThat(locationList.getLocations().isEmpty()).isFalse();
        assertThat(locationList.getLocations().containsAll(this.locationList)).isTrue();

    }

    @Test
    void getLocations_empty_returnsNotFound() {
        when(this.locationRepository.findAll()).thenReturn(new ArrayList<>());
        assertThatExceptionOfType(LocationNotFoundException.class).isThrownBy(() -> this.locationService.getLocations());
    }

    @Test
    void getLocation_withParamId_exists_returnsLocation() {
        final int TEST_ID = 1;
        when(this.locationRepository.findById(TEST_ID)).thenReturn(Optional.of(this.locationList.get(TEST_ID)));
        Location location = this.locationService.getLocation(TEST_ID);
        assertThat(location).isNotNull();
        assertThat(location.getOwner().contains("testOwner")).isTrue();
    }

    @Test
    void getLocation_withParamId_notExists_returnsNotFound() {
        when(this.locationRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThatExceptionOfType(LocationNotFoundException.class).isThrownBy(() -> this.locationService.getLocation(anyInt()));
    }


    @Test
    void postLocation_valid_returnsLocation() {
        Location location = new Location(1, "testOwner", 1, 1);
        when(this.locationRepository.save(location)).thenReturn(location);
        assertThat(this.locationService.postLocation(location)).isEqualTo(location);
    }


    @Test
    void patchLocation_exists_valid_returnLocation() {
        final String NEW_OWNER = "newOwner";
        Location location = new Location(1, "testOwner", 1, 1);
        when(this.locationRepository.findById(location.getId())).thenReturn(Optional.of(location));
        when(this.locationRepository.save(location)).thenReturn(location);
        final String ACTUAL_OWNER = this.locationService.patchLocation(location.getId(), NEW_OWNER).getOwner();
        assertThat(ACTUAL_OWNER).isEqualTo(NEW_OWNER);
    }

    @Test
    void patchLocation_notExists_valid_returnNotFound() {
        when(this.locationRepository.findById(anyInt())).thenReturn(Optional.empty());
        // TODO: Check raw/any value exception in lambda (why does this work?)
        assertThatExceptionOfType(LocationNotFoundException.class).isThrownBy(() -> this.locationService.patchLocation(anyInt(),"gibberish"));
    }

    @Test
    void deleteLocation_exists_returnOk() {
        Location location = new Location(1, "testOwner", 1, 1);
        when(this.locationRepository.findById(anyInt())).thenReturn(Optional.of(location));
        this.locationService.deleteLocation(anyInt());
        verify(locationRepository).delete(any(Location.class));
    }

    @Test
    void deleteLocation_notExists_returnNotFound() {
        assertThatExceptionOfType(LocationNotFoundException.class).isThrownBy(() -> this.locationService.deleteLocation(anyInt()));
    }
}
