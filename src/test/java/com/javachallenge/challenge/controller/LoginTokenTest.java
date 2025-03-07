package com.javachallenge.challenge.controller;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class LoginTokenTest {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	private LoginTokenTest() {
	}

	public static String getTokenForLogin(String username, String password, MockMvc mockMvc) throws Exception {
		String content = mockMvc.perform(post("/api/auth")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"password\": \"" + password + "\", \"username\": \"" + username + "\"}"))
				.andReturn().getResponse().getContentAsString();

		AuthenticationResponse authResponse = objectMapper.readValue(content, AuthenticationResponse.class);
		return authResponse.getAccessToken();
	}

	private static class AuthenticationResponse {

		@JsonAlias("access_token")
		private String accessToken;

		public String getAccessToken() {
			return accessToken;
		}
	}
	
}
