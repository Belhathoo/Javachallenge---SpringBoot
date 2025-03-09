package com.javachallenge.challenge.controller;

import com.javachallenge.challenge.model.AppUser;
import com.javachallenge.challenge.model.UserRole;
import com.javachallenge.challenge.repository.UserRepository;
import com.javachallenge.challenge.service.UserService;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

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
	UserService userService;
	@Autowired
	UserRepository repository;
	@Autowired
	PasswordEncoder passwordEncoder;

	String users;
	private final String username = "userUsername";
	private final String adminUsername = "adminUsername";
	private final String emailAdmin = "admin@mail.com";
	private final String email = "test@mail.com";
	private final String password = "password";

	@PostConstruct
	void setUp() {
		users = userService.userGenerate(1);
		repository.deleteAll();
		repository.save(new AppUser(adminUsername, passwordEncoder.encode(password), emailAdmin, UserRole.ADMIN));
		repository.save(new AppUser(username, passwordEncoder.encode(password), email, UserRole.USER));
	}

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
	void testGenerateUsersWithInvalidCountFormat() throws Exception {

		mockMvc.perform(get("/api/users/generate")
						.param("count", "abc")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testBatch() throws Exception {

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
	void testBatchWithEmptyFile() throws Exception {
		MockMultipartFile emptyFile = new MockMultipartFile("file", "empty.json", "application/json", "".getBytes());

		mockMvc.perform(multipart("/api/users/batch").file(emptyFile)
						.contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testBatchWithInvalidFileFormat() throws Exception {
		MockMultipartFile invalidFile = new MockMultipartFile(
				"file",
				"invalid.txt",
				"text/plain",
				"This is not a JSON file".getBytes()
		);

		mockMvc.perform(multipart("/api/users/batch")
						.file(invalidFile)
						.contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testBatchWithInvalidJsonData() throws Exception {
		String invalidJson = "[{\"username\": \"user1\", \"password\": \"pass123\" "; // Missing closing brace

		MockMultipartFile malformedJsonFile = new MockMultipartFile(
				"file",
				"invalid.json",
				"application/json",
				invalidJson.getBytes()
		);

		mockMvc.perform(multipart("/api/users/batch")
						.file(malformedJsonFile)
						.contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testGetMyProfile() throws Exception {
		String token = getTokenForLogin(username, password, mockMvc);

		mockMvc.perform(get("/api/users/me")
						.header("Authorization", "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username", is(username)))
				.andExpect(jsonPath("$.email", is(email)));

	}

	@Test
	void testGetUserProfileByAdmin() throws Exception {
		String token = getTokenForLogin(adminUsername, password, mockMvc);

		mockMvc.perform(get("/api/users/" + username)
						.header("Authorization", "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username", is(username)))
				.andExpect(jsonPath("$.email", is(email)));
	}

	@Test
	void testGetInvalidUserProfileByAdmin() throws Exception {
		String token = getTokenForLogin(adminUsername, password, mockMvc);

		mockMvc.perform(get("/api/users/" + "Inavalid_username")
						.header("Authorization", "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	void testGetUserProfileByNonAdmin() throws Exception {
		String token = getTokenForLogin(username, password, mockMvc);

		mockMvc.perform(get("/api/users/" + adminUsername)
						.header("Authorization", "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());
	}

}
