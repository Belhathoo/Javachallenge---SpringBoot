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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    void testAuthWithValidUsernameAndPassword() throws Exception {

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


        mockMvc.perform(post("/api/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"password\": \"" + user.getPassword() + "\", \"username\": \"" + user.getUsername() + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists());
    }

    @Test
    void testAuthWithUsername() throws Exception {
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
                .andExpect(status().isOk());

    }


    @Test
    void testAuthWithEmail() throws Exception {
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

        String token = getTokenForLogin(user.getEmail(), user.getPassword(), mockMvc);

        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    void testAuthWithInvalidUsername() throws Exception {
        mockMvc.perform(post("/api/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"invalid_user\",\"password\":\"password\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid username or password"));
    }

    @Test
    void testAuthWithInvalidPassword() throws Exception {
        mockMvc.perform(post("/api/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"valid_user\",\"password\":\"wrongpas\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid username or password"));
    }

    @Test
    void testWithMalformedToken() throws Exception {
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + "ey,qmlcjqslkjcidychqncked;qsmdipqkdcxbq,x")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
