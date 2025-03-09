package com.javachallenge.challenge.controller;

import com.javachallenge.challenge.exceptions.UserNotFoundException;
import com.javachallenge.challenge.model.AppUser;
import com.javachallenge.challenge.model.UserRole;
import com.javachallenge.challenge.repository.UserRepository;
import com.javachallenge.challenge.security.jwt.JwtGenerate;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static com.javachallenge.challenge.controller.LoginTokenTest.getTokenForLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserRepository repository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    JwtGenerate jwtGenerate;

    private final String username = "testUsername";
    private final String password = "password";
    private final String email = "test@mail.com";

    @PostConstruct
    void setUp() {
        repository.deleteAll();
        AppUser appUser = new AppUser(username, passwordEncoder.encode(password), email, UserRole.USER);
        repository.save(appUser);
    }

    @Test
    void testAuthWithValidUsernameAndPassword() throws Exception {

        if (repository.existsByUsername(username))
            throw (new UserNotFoundException("No user in the Database"));

        mockMvc.perform(post("/api/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"password\": \"" + password + "\", \"username\": \"" + username + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists());
    }

    @Test
    void testAuthWithValidEmailAndValidPassword() throws Exception {
        if (repository.existsByUsername(username))
            throw (new UserNotFoundException("No user in the Database"));

        mockMvc.perform(post("/api/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"password\": \"" + password + "\", \"username\": \"" + email + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists());
    }

    @Test
    void testAuthWithInvalidUsername() throws Exception {
        mockMvc.perform(post("/api/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"invalid_user\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid username or password"));
    }

    @Test
    void testAuthWithEmptyUsername() throws Exception {
        mockMvc.perform(post("/api/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"password\": \"" + password + "\", \"username\": \"\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid username or password"));
    }

    @Test
    void testAuthWithInvalidPassword() throws Exception {
        mockMvc.perform(post("/api/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"password\": \"000000\", \"username\": \"" + username + "\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid username or password"));
    }

    @Test
    void testAuthWithEmptyPassword() throws Exception {
        mockMvc.perform(post("/api/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"password\": \"\", \"username\": \"" + username + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Invalid request content."));
    }

    @Test
    void testAuthWithMalformedJson() throws Exception {
        mockMvc.perform(post("/api/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"" + username + "\", \"password\": "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Failed to read request"));
    }


    @Test
    void testTokenValidity() throws Exception {
        String token = getTokenForLogin(username, password, mockMvc);

        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    void testUnauthorizedAccessWithMalformedToken() throws Exception {
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + "ey,qmlcjqslkjcidychqncked;qsmdipqkdcxbq,x")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUnauthorizedAccessWithoutToken() throws Exception {
        mockMvc.perform(get("/api/users/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testWithExpiredToken() throws Exception {
        String expiredToken = jwtGenerate.generateExpiredToken(username, email);

        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + expiredToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

}
