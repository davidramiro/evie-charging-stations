package de.volkswagen.springevchargingapi;

import de.volkswagen.springevchargingapi.model.Location;
import de.volkswagen.springevchargingapi.model.LocationList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LocationController.class)
public class LocationControllerTests {
    @Autowired
    MockMvc mockMvc;

    ObjectMapper mapper = new ObjectMapper();

    @MockBean
    LocationService locationService;

    private List<Location> locationList;
    private final int LOCATION_COUNT = 100;


    @BeforeEach
    void setUp() {
        this.locationList = new ArrayList<Location>();
        for(int i=1; i <= LOCATION_COUNT; i++) {
            Location location = new Location(i, "testOwner" + i, LOCATION_COUNT-i, i);
            this.locationList.add(location);
        }
    }

    @Test
    void getLocations_noParams_exists_returnLocationList() throws Exception {
        when(locationService.getLocations()).thenReturn(new LocationList(this.locationList));
        mockMvc.perform(get("/api/location/stations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.locations", hasSize(LOCATION_COUNT)));
    }

    @Test
    void getLocations_empty_returnsNotFound() throws Exception {
        when(locationService.getLocations()).thenThrow(LocationNotFoundException.class);

        mockMvc.perform(get("/api/location/stations"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getLocation_withParamId_exists_returnsLocation() throws Exception {
        Location location = new Location(1, "testOwner", 2, 3);
        when(locationService.getLocation(anyInt())).thenReturn(location);
        mockMvc.perform(get("/api/location/station/" + 1, Location.class))
                .andExpect(status().isOk())
                .andExpect(jsonPath("owner").value(location.getOwner()));
    }

    @Test
    void getLocation_withParamId_notExists_returnsNotFound() throws Exception {
        when(locationService.getLocation(anyInt())).thenThrow(LocationNotFoundException.class);

        mockMvc.perform(get("/api/location/station/5"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getLocation_withInvalidId_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/location/station/something"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postLocation_valid_returnsLocation() throws Exception {
        Location location = new Location(1, "testOwner", 2, 3);
        when(locationService.postLocation(any(Location.class))).thenReturn(location);
        mockMvc.perform(post("/api/location/stations")
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(location)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("owner").value("testOwner"))
                .andExpect(jsonPath("latitude").value(2));

    }

    @Test
    void postLocation_invalid_returnsBadRequest() throws Exception {
        when(locationService.postLocation(new Location())).thenThrow(InvalidParameterException.class);
        mockMvc.perform(post("/api/location/stations").contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString("invalidInput")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void patchLocation_exists_valid_returnLocation() throws Exception {
        Location location = new Location(1, "testOwner", 2, 3);
        when(locationService.patchLocation(anyInt(), anyString())).thenReturn(location);
        mockMvc.perform(patch("/api/location/station/" + location.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"owner\":\"newOwner\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("owner").value("newOwner"));

    }

    @Test
    void patchLocation_notExists_valid_returnNotFound() throws Exception{
        Location location = new Location(1, "testOwner", 2, 3);
        when(locationService.patchLocation(anyInt(), anyString())).thenThrow(LocationNotFoundException.class);
        mockMvc.perform(patch("/api/location/station/" + location.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"owner\":\"newOwner\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void patchLocation_invalid_returnBadRequest() throws Exception{
        mockMvc.perform(patch("/api/location/station/1").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString("invalid")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteLocation_exists_returnOk() throws Exception {
        mockMvc.perform(delete("/api/location/station/1"))
                .andExpect(status().isOk());
        verify(locationService).deleteLocation(anyInt());
    }

    @Test
    void deleteLocation_notExists_returnNotFound() throws Exception {
        doThrow(new LocationNotFoundException()).when(locationService).deleteLocation(anyInt());
        mockMvc.perform(delete("/api/location/station/1"))
                .andExpect(status().isNotFound());
        verify(locationService).deleteLocation((anyInt()));
    }

    @Test
    void deleteLocation_invalid_returnBadRequest() throws Exception {
        mockMvc.perform(delete("/api/location/station/bad"))
                .andExpect(status().isBadRequest());
    }
}
