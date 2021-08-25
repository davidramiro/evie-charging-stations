package de.volkswagen.springevchargingflagsapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.volkswagen.springevchargingflagsapi.model.Flag;
import de.volkswagen.springevchargingflagsapi.model.FlagList;
import de.volkswagen.springevchargingflagsapi.model.StatusEnum;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;

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
	FlagRepository flagRepository;

	ObjectMapper mapper = new ObjectMapper();

	@Test
	void contextLoads() {
	}

	@Test
	void getFlags_exists_returnsFlagList() {
		Flag flag = new Flag(1, StatusEnum.IN_USE, 100f, 100f, 100f, 100f);
		Flag flag2 = new Flag(1, StatusEnum.IN_USE, 100f, 100f, 100f, 100f);

		this.flagRepository.saveAll(Arrays.asList(flag, flag2));

		ResponseEntity<FlagList> responseEntity = restTemplate.getForEntity("/api/flag/flags", FlagList.class);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		FlagList responseBody = responseEntity.getBody();
		assertThat(responseBody).isNotNull();
		List<Flag> responseFlag = responseBody.getFlags();
		assertThat(responseFlag.isEmpty()).isFalse();

		this.flagRepository.flush();
		this.flagRepository.deleteAll();
	}


	@Test
	void getFlags_notExists_returnsNotFound() {
		Flag flag = new Flag(1, StatusEnum.IN_USE, 100f, 100f, 100f, 100f);
		Flag flag2 = new Flag(1, StatusEnum.IN_USE, 100f, 100f, 100f, 100f);
		this.flagRepository.saveAll(Arrays.asList(flag, flag2));

		this.flagRepository.deleteAll();
		ResponseEntity<FlagList> responseEntity = restTemplate.getForEntity("/api/flag/flags", FlagList.class);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

		this.flagRepository.flush();
	}

	@Test
	void getFlag_withParamId_exists_returnsFlag() {
		Flag flag = new Flag(1, StatusEnum.IN_USE, 100f, 100f, 100f, 100f);
		Flag flag2 = new Flag(1, StatusEnum.IN_USE, 100f, 100f, 100f, 100f);
		List<Flag> testList = this.flagRepository.saveAll(Arrays.asList(flag, flag2));

		ResponseEntity<String> responseEntity = restTemplate.getForEntity("/api/flag/flag/" + testList.get(0).getId(), String.class);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		String responseBody = responseEntity.getBody();
		assertThat(responseBody)
				.isNotNull()
				.isEqualTo(String.format("{\"id\":%d,\"locationid\":1,\"status\":\"in-use\",\"ccs2\":100.0,\"type2\":100.0,\"chademo\":100.0,\"tesla\":100.0}", testList.get(0).getId()));

		this.flagRepository.flush();
		this.flagRepository.deleteAll();
	}

	@Test
	void getFlag_withParamId_notExists_returnsNotFound() {
		Flag flag = new Flag(1, StatusEnum.IN_USE, 100f, 100f, 100f, 100f);
		Flag flag2 = new Flag(1, StatusEnum.IN_USE, 100f, 100f, 100f, 100f);
		this.flagRepository.saveAll(Arrays.asList(flag, flag2));

		ResponseEntity<String> responseEntity = restTemplate.getForEntity("/api/flag/flag/65535", String.class);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

		this.flagRepository.flush();
		this.flagRepository.deleteAll();
	}

	@Test
	void getFlag_withInvalidId_returnsBadRequest() {
		ResponseEntity<String> responseEntity = restTemplate.getForEntity("/api/flag/flag/gibberish", String.class);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	void postFlag_valid_returnsFlag() {
		Flag flag = new Flag(1, StatusEnum.IN_USE, 100f, 100f, 100f, 100f);
		Flag flag2 = new Flag(1, StatusEnum.IN_USE, 100f, 100f, 100f, 100f);
		this.flagRepository.saveAll(Arrays.asList(flag, flag2));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<Flag> request = new HttpEntity<>(flag, headers);
		ResponseEntity<Flag> responseEntity = restTemplate.postForEntity("/api/flag/flags", request, Flag.class);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		Flag responseBody = responseEntity.getBody();
		assertThat(responseBody).isNotNull();
		assertThat(responseBody.getCcs2()).isEqualTo(flag.getCcs2());


		this.flagRepository.flush();
		this.flagRepository.deleteAll();
	}

	@Test
	void postFlag_invalid_returnsBadRequest() {
		// TODO: Check response in impl
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<>("{\"some\":\"bad\",\"json\":\"data\",\"right\":\"here\"}", headers);
		ResponseEntity<String> responseEntity = restTemplate.postForEntity("/api/flag/flags", request, String.class);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	void putFlag_exists_valid_returnFlag() throws JsonProcessingException {
		final Flag newFlag = new Flag(5, StatusEnum.MAINTENANCE, 10f, 10f, 10f, 10f);

		Flag flag = new Flag(1, StatusEnum.IN_USE, 100f, 100f, 100f, 100f);
		Flag flag2 = new Flag(1, StatusEnum.IN_USE, 100f, 100f, 100f, 100f);
		List<Flag> testList = this.flagRepository.saveAll(Arrays.asList(flag, flag2));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		String requestJson = mapper.writeValueAsString(newFlag);
		HttpEntity<String> request = new HttpEntity<>(requestJson, headers);

		ResponseEntity<String> responseEntity = restTemplate.exchange("/api/flag/flag/" + testList.get(0).getId(), HttpMethod.PUT, request, String.class);
		String responseBody = responseEntity.getBody();

		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseBody)
				.isNotNull()
				.isEqualTo(String.format("{\"id\":%d,\"locationid\":5,\"status\":\"maintenance\",\"ccs2\":10.0,\"type2\":10.0,\"chademo\":10.0,\"tesla\":10.0}", testList.get(0).getId()));

		this.flagRepository.flush();
		this.flagRepository.deleteAll();
	}

	@Test
	void putFlag_notExists_valid_returnNotFound() throws JsonProcessingException {
		final int TEST_FLAG_ID = 0;
		final Flag newFlag = new Flag(5, StatusEnum.MAINTENANCE, 10f, 10f, 10f, 10f);
		String requestJson = mapper.writeValueAsString(newFlag);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> request = new HttpEntity<>(requestJson, headers);

		ResponseEntity<String> responseEntity = restTemplate.exchange("/api/flag/flag/" + TEST_FLAG_ID, HttpMethod.PUT, request, String.class);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void patchFlag_invalid_returnBadRequest() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		String requestJson = "{\"owner\":\"jasdfasdf\"}";
		HttpEntity<String> request = new HttpEntity<>(requestJson, headers);
		ResponseEntity<String> responseEntity = restTemplate.exchange("/api/flag/flag/gibberish", HttpMethod.PUT, request, String.class);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	void deleteFlag_exists_returnOk() {
		Flag flag = new Flag(1, StatusEnum.IN_USE, 100f, 100f, 100f, 100f);
		Flag flag2 = new Flag(1, StatusEnum.IN_USE, 100f, 100f, 100f, 100f);
		List<Flag> testList = this.flagRepository.saveAll(Arrays.asList(flag, flag2));

		ResponseEntity<String> response = restTemplate.getForEntity(String.format("/api/flag/flag/%d", testList.get(0).getId()), String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		String EXPECTED_JSON = "{\"id\":%d,\"locationid\":1,\"status\":\"in-use\",\"ccs2\":100.0,\"type2\":100.0,\"chademo\":100.0,\"tesla\":100.0}";
		assertThat(response.getBody()).isEqualTo(String.format(EXPECTED_JSON, testList.get(0).getId()));

		restTemplate.delete(String.format("/api/flag/flag/%d", testList.get(0).getId()));

		ResponseEntity<String> deletedResponse = restTemplate.getForEntity(String.format("/api/flag/flag/%d", testList.get(0).getId()), String.class);
		assertThat(deletedResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

		this.flagRepository.flush();
		this.flagRepository.deleteAll();

	}

	@Test
	void deleteFlag_notExists_returnNotFound() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<>(null, headers);
		ResponseEntity<String> responseEntity = restTemplate.exchange("/api/flag/flag/6353", HttpMethod.DELETE, request, String.class);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void deleteFlag_invalid_returnBadRequest() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<>(null, headers);
		ResponseEntity<String> responseEntity = restTemplate.exchange("/api/flag/flag/NotAnInteger", HttpMethod.DELETE, request, String.class);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}
}