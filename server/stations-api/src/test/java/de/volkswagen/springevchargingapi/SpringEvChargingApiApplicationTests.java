package de.volkswagen.springevchargingapi;

import de.volkswagen.springevchargingapi.model.Location;
import de.volkswagen.springevchargingapi.model.LocationList;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// TODO: Single tests pass, as part of suite they fail? Why?
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
class SpringEvChargingApiApplicationTests {
	@Autowired
	TestRestTemplate restTemplate;
	@Autowired
	LocationRepository locationRepository;

	@Test
	void contextLoads() {
	}


	@Test
	void getLocations_exists_returnsLocationList() {
		Location location = new Location(1, "firstOwner", 50.02, 9.08);
		Location location2 = new Location(2, "secondOwner", 51.02, 10.08);

		this.locationRepository.saveAll(Arrays.asList(location, location2));

		ResponseEntity<LocationList> responseEntity = restTemplate.getForEntity("/api/location/stations", LocationList.class);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		LocationList responseBody = responseEntity.getBody();
		assertThat(responseBody).isNotNull();
		List<Location> responseLocation = responseBody.getLocations();
		assertThat(responseLocation.isEmpty()).isFalse();

		this.locationRepository.flush();
		this.locationRepository.deleteAll();
	}


	@Test
	void getLocations_notExists_returnsNotFound() {
		Location location = new Location(1, "firstOwner", 50.02, 9.08);
		Location location2 = new Location(2, "secondOwner", 51.02, 10.08);

		this.locationRepository.saveAll(Arrays.asList(location, location2));
		this.locationRepository.deleteAll();

		ResponseEntity<LocationList> responseEntity = restTemplate.getForEntity("/api/location/stations", LocationList.class);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

		this.locationRepository.flush();
	}

	@Test
	void getLocation_withParamId_exists_returnsLocation() {
		Location location = new Location(1, "testOwner", 50.02, 9.08);
		Location location2 = new Location(2, "testOwner", 50.02, 9.08);

		List<Location> testList = this.locationRepository.saveAll(Arrays.asList(location, location2));

		ResponseEntity<String> responseEntity = restTemplate.getForEntity("/api/location/station/" + testList.get(0).getId(), String.class);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		String responseBody = responseEntity.getBody();
		assertThat(responseBody)
				.isNotNull()
				.isEqualTo(String.format("{\"id\":%d,\"owner\":\"testOwner\",\"latitude\":50.02,\"longitude\":9.08}", testList.get(0).getId()));

		this.locationRepository.flush();
		this.locationRepository.deleteAll();
	}

	@Test
	void getLocation_withParamId_notFound_returnsNotFound() {
		ResponseEntity<Location> responseEntity = restTemplate.getForEntity("/api/location/station/65535", Location.class);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void getLocation_withInvalidId_returnsBadRequest() {
		ResponseEntity<Location> responseEntity = restTemplate.getForEntity("/api/location/station/gibberish", Location.class);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	void postLocation_valid_returnsLocation() {
		Location location = new Location(1, "testOwner", 50.02, 9.08);
		Location location2 = new Location(2, "testOwner", 50.02, 9.08);

		this.locationRepository.saveAll(Arrays.asList(location, location2));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		Location testLocation = new Location(3, "otherOwner", 53.02, 9.18);
		HttpEntity<Location> request = new HttpEntity<>(testLocation, headers);
		ResponseEntity<Location> responseEntity = restTemplate.postForEntity("/api/location/stations", request, Location.class);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		Location responseBody = responseEntity.getBody();
		assertThat(responseBody).isNotNull();
		assertThat(responseBody.getLatitude()).isEqualTo(testLocation.getLatitude());

		this.locationRepository.flush();
		this.locationRepository.deleteAll();
	}

	@Test
	void postLocation_invalid_returnsBadRequest() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<>("{\"some\":\"bad\",\"json\":\"data\",\"right\":\"here\"}", headers);
		ResponseEntity<Location> responseEntity = restTemplate.postForEntity("/api/location/stations/", request, Location.class);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	void patchLocation_exists_valid_returnLocation() {
		Location location = new Location(1, "testOwner", 50.02, 9.08);
		Location location2 = new Location(2, "testOwner", 50.02, 9.08);

		List<Location> testList = this.locationRepository.saveAll(Arrays.asList(location, location2));

		final String TEST_NEW_OWNER = "newOwner";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		String requestJson = String.format("{\"owner\":\"%s\"}", TEST_NEW_OWNER);
		HttpEntity<String> request = new HttpEntity<>(requestJson, headers);

		ResponseEntity<Location> responseEntity = restTemplate.exchange("/api/location/station/" + testList.get(0).getId(), HttpMethod.PATCH, request, Location.class);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		Location responseBody = responseEntity.getBody();
		assertThat(responseBody).isNotNull();
		assertThat(responseBody.getOwner()).isEqualTo(TEST_NEW_OWNER);

		this.locationRepository.flush();
		this.locationRepository.deleteAll();
	}

	@Test
	void patchLocation_notExists_valid_returnNotFound() {
		final String TEST_NEW_OWNER = "newOwner";
		String requestJson = String.format("{\"owner\":\"%s\"}", TEST_NEW_OWNER);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> request = new HttpEntity<>(requestJson, headers);

		ResponseEntity<Location> responseEntity = restTemplate.exchange("/api/location/station/65535", HttpMethod.PATCH, request, Location.class);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void patchLocation_invalid_returnBadRequest() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		String requestJson = "{\"owner\":\"jasdfasdf\"}";
		HttpEntity<String> request = new HttpEntity<>(requestJson, headers);
		ResponseEntity<Location> responseEntity = restTemplate.exchange("/api/location/station/gibberish", HttpMethod.PATCH, request, Location.class);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

	}

	@Test
	void deleteLocation_exists_returnOk() {
		Location location = new Location(1, "testOwner", 50.02, 9.08);
		Location location2 = new Location(2, "testOwner", 50.02, 9.08);

		List<Location> testList = this.locationRepository.saveAll(Arrays.asList(location, location2));

		ResponseEntity<String> response = restTemplate.getForEntity(String.format("/api/location/station/%s", testList.get(0).getId()), String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody())
				.isNotNull()
				.isEqualTo(String.format("{\"id\":%d,\"owner\":\"testOwner\",\"latitude\":50.02,\"longitude\":9.08}", testList.get(0).getId()));

		restTemplate.delete(String.format("/api/location/station/%d", testList.get(0).getId()));

		ResponseEntity<Location> deletedResponse = restTemplate.getForEntity(String.format("/api/location/station/%d", testList.get(0).getId()), Location.class);
		assertThat(deletedResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

		this.locationRepository.flush();
		this.locationRepository.deleteAll();
	}

	@Test
	void deleteLocation_notExists_returnNotFound() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<>(null, headers);
		ResponseEntity<Location> responseEntity = restTemplate.exchange("/api/location/station/65535", HttpMethod.DELETE, request, Location.class);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void deleteLocation_invalid_returnBadRequest() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<>(null, headers);
		ResponseEntity<Location> responseEntity = restTemplate.exchange("/api/location/station/NotAnInteger", HttpMethod.DELETE, request, Location.class);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}
}