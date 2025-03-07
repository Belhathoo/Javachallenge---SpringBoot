package com.javachallenge.challenge.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javachallenge.challenge.dto.AppUserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.javachallenge.challenge.controller.LoginTokenTest.getTokenForLogin;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UsersControllerTest {

	@Autowired
	MockMvc mockMvc;
	@Autowired
	ObjectMapper objectMapper;

	@Test
	void generateUsers() throws Exception {

		mockMvc.perform(get("/api/users/generate")
				.param("count", "5")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));
	}

	@Test
	void generateUsersWithNegativeCount() throws Exception {

		mockMvc.perform(get("/api/users/generate")
				.param("count", "-5")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testGenerateUsersWithNoCount() throws Exception {
		mockMvc.perform(get("/api/users/generate")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.detail", is("Required parameter 'count' is not present.")));
	}

	@Test
	void testBatch() throws Exception {

		String users = mockMvc.perform(get("/api/users/generate")
				.param("count", "1")
				.contentType(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();

		MockMultipartFile multipartFile = new MockMultipartFile("file", users.getBytes());
		mockMvc.perform(multipart("/api/users/batch").file(multipartFile)
				.contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.total", is(1)))
				.andExpect(jsonPath("$.imported", is(1)))
				.andExpect(jsonPath("$.nonImported", is(0)));
	}

	@Test
	void testBatchSameUser() throws Exception {
		String users = mockMvc.perform(get("/api/users/generate")
				.param("count", "1")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();

		MockMultipartFile multipartFile = new MockMultipartFile("file", users.getBytes());

		mockMvc.perform(multipart("/api/users/batch").file(multipartFile)
				.contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

		mockMvc.perform(multipart("/api/users/batch").file(multipartFile)
				.contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.total", is(1)))
				.andExpect(jsonPath("$.imported", is(0)))
				.andExpect(jsonPath("$.nonImported", is(1)));
	}

	@Test
	void testBatchUserWithInvalidPassword() throws Exception {

		String users = mockMvc.perform(get("/api/users/generate")
				.param("count", "1")
				.contentType(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();

		String newUsers = users.replaceAll("\"password\": \"[^\"]+\"", "\"password\": \"123\"");
		MockMultipartFile multipartFile = new MockMultipartFile("file", newUsers.getBytes());

		mockMvc.perform(multipart("/api/users/batch").file(multipartFile)
				.contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.total", is(1)))
				.andExpect(jsonPath("$.imported", is(0)))
				.andExpect(jsonPath("$.nonImported", is(1)));
	}


	@Test
	void testGetMyProfile() throws Exception {
		String users = mockMvc.perform(get("/api/users/generate")
				.param("count", "1")
				.contentType(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();

		List<AppUserDto> usersList = objectMapper.readValue(users, new TypeReference<>() {
        });
		AppUserDto user = usersList.get(0);
		MockMultipartFile multipartFile = new MockMultipartFile("file", users.getBytes());

		mockMvc.perform(multipart("/api/users/batch").file(multipartFile)
				.contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

		String token = getTokenForLogin(user.getUsername(), user.getPassword(), mockMvc);

		mockMvc.perform(get("/api/users/me")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username", is(user.getUsername())))
				.andExpect(jsonPath("$.email", is(user.getEmail())));

	}

	@Test
	void testGetUserProfileByAdmin() throws Exception {
		String users = mockMvc.perform(get("/api/users/generate")
				.param("count", "2")
				.contentType(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();

		String newUsers = users.replaceAll("\"role\": \"[^\"]+\"", "\"role\": \"ADMIN\"");

		List<AppUserDto> usersList = objectMapper.readValue(newUsers, new TypeReference<>() {
        });

		AppUserDto user1 = usersList.get(0);
		AppUserDto user2 = usersList.get(1);
		MockMultipartFile multipartFile = new MockMultipartFile("file", newUsers.getBytes());

		mockMvc.perform(multipart("/api/users/batch").file(multipartFile)
				.contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

		String token = getTokenForLogin(user1.getUsername(), user1.getPassword(), mockMvc);

		mockMvc.perform(get("/api/users/" + user2.getUsername())
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username", is(user2.getUsername())))
				.andExpect(jsonPath("$.email", is(user2.getEmail())));
	}

	@Test
	void testGetInvalidUserProfileByAdmin() throws Exception {
		String users = mockMvc.perform(get("/api/users/generate")
						.param("count", "2")
						.contentType(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();

		String newUsers = users.replaceAll("\"role\": \"[^\"]+\"", "\"role\": \"ADMIN\"");

		List<AppUserDto> usersList = objectMapper.readValue(newUsers, new TypeReference<>() {
		});

		AppUserDto user1 = usersList.get(0);
		MockMultipartFile multipartFile = new MockMultipartFile("file", newUsers.getBytes());

		mockMvc.perform(multipart("/api/users/batch").file(multipartFile)
				.contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

		String token = getTokenForLogin(user1.getUsername(), user1.getPassword(), mockMvc);

		mockMvc.perform(get("/api/users/" + "Inavalid_username")
						.header("Authorization", "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	void testGetUserProfileByNonAdmin() throws Exception {
		String users = mockMvc.perform(get("/api/users/generate")
				.param("count", "2")
				.contentType(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();

		String newUsers = users.replaceAll("\"role\": \"[^\"]+\"", "\"role\": \"USER\"");

		List<AppUserDto> usersList = objectMapper.readValue(newUsers, new TypeReference<>() {
        });

		AppUserDto user1 = usersList.get(0);
		AppUserDto user2 = usersList.get(1);
		MockMultipartFile multipartFile = new MockMultipartFile("file", newUsers.getBytes());

		mockMvc.perform(multipart("/api/users/batch").file(multipartFile)
				.contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

		String token = getTokenForLogin(user1.getUsername(), user1.getPassword(), mockMvc);

		mockMvc.perform(get("/api/users/" + user2.getUsername())
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());
	}

	@Test
	void testBatchWithEmptyFile() throws Exception {
		MockMultipartFile emptyFile = new MockMultipartFile("file", "empty.json", "application/json", "".getBytes());

		mockMvc.perform(multipart("/api/users/batch").file(emptyFile)
						.contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testUnauthorizedAccessWithoutToken() throws Exception {
		mockMvc.perform(get("/api/users/me")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized());
	}
}
