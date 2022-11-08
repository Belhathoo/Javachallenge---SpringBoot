package com.javachallenge.challenge.controller;

import static com.javachallenge.challenge.controller.LoginTokenTest.getTokenForLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javachallenge.challenge.dto.AppUserDto;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

	@Autowired
	MockMvc mockMvc;
	@Autowired
	ObjectMapper objectMapper;

	@Test
	void testAuthWithUsername() throws Exception {
		String users = mockMvc.perform(get("/api/users/generate")
				.param("count", "1")
				.contentType(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();

		List<AppUserDto> usersList = objectMapper.readValue(users, new TypeReference<List<AppUserDto>>() {
		});
		AppUserDto user = usersList.get(0);
		MockMultipartFile multipartFile = new MockMultipartFile("file", users.getBytes());

		mockMvc.perform(fileUpload("/api/users/batch").file(multipartFile)
				.contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

		String token = getTokenForLogin(user.getUsername(), user.getPassword(), mockMvc);

		mockMvc.perform(get("/api/users/me")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isAccepted());

	}

	@Test
	void testAuthWithEmail() throws Exception {
		String users = mockMvc.perform(get("/api/users/generate")
				.param("count", "1")
				.contentType(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();

		List<AppUserDto> usersList = objectMapper.readValue(users, new TypeReference<List<AppUserDto>>() {
		});
		AppUserDto user = usersList.get(0);
		MockMultipartFile multipartFile = new MockMultipartFile("file", users.getBytes());

		mockMvc.perform(fileUpload("/api/users/batch").file(multipartFile)
				.contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

		String token = getTokenForLogin(user.getEmail(), user.getPassword(), mockMvc);

		mockMvc.perform(get("/api/users/me")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isAccepted());

	}
}
