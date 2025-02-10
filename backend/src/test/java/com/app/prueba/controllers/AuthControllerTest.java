package com.app.prueba.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.app.prueba.models.User;
import com.app.prueba.services.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private User testUser;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();

        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("testuser@gmail.com");
        testUser.setPassword("password");
        testUser.setName("Test");
        testUser.setLastName("User");
        testUser.setPhone("1234567890");
    }

    @Test
    public void testRegister() throws Exception {
        when(authService.register(any(User.class))).thenReturn(new HashMap<>());

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(testUser)))
                .andExpect(status().isCreated());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/invalid_users.csv", numLinesToSkip = 1)
    public void testRegisterInvalid(String name, String lastName, String email, String username, String password,
            String phone, String expectedField, String expectedMessage) throws Exception {

        when(authService.register(any(User.class))).thenReturn(new HashMap<>());

        User invalidUser = createUser(name, lastName, email, username, password, phone);
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$." + expectedField).value(expectedMessage));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/unique_users.csv", numLinesToSkip = 1)
    public void testRegisterUniqueInvalid(String name, String lastName, String email, String username, String password,
            String phone) throws Exception {

        when(authService.register(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("Detail: Unique constraint violation"));

        User invalidUser = createUser(name, lastName, email, username, password, phone);
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(invalidUser)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Unique constraint violation"));
    }

    private static User createUser(String name, String lastName, String email, String username, String password,
            String phone) {
        User user = new User();
        user.setName(name);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(password);
        user.setPhone(phone);
        return user;
    }

    @Test
    public void testLogin() throws Exception {
        when(authService.login(any(User.class))).thenReturn(new HashMap<>());

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(testUser)))
                .andExpect(status().isOk());
    }

    @Test
    public void testLoginInvalidPassword() throws Exception {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Invalid password");

        when(authService.login(any(User.class))).thenReturn(response);

        testUser.setPassword("wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(testUser)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid password"));
    }

    @Test
    public void testLoginUserNotFound() throws Exception {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Username or email not found");

        when(authService.login(any(User.class))).thenReturn(response);

        testUser.setUsername("nonexistent");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(testUser)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Username or email not found"));
    }
}