package com.app.prueba.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.Collections;
import java.util.HashMap;

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

import com.app.prueba.models.Cards;
import com.app.prueba.models.User;
import com.app.prueba.models.UserCards;
import com.app.prueba.services.UserService;
import com.app.prueba.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private Utils utils;

    @MockitoBean
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User testUser;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        testUser = new User();
        testUser.setId(1);
        testUser.setName("Test");
        testUser.setLastName("User");
        testUser.setEmail("testusernew2@gmail.com");
        testUser.setUsername("testusernew2");
        testUser.setPassword("password");
        testUser.setPhone("1234567890");

        when(utils.createUser("Test", "User", "testusernew2@gmail.com", "testusernew2", "password", "1234567890"))
                .thenReturn(testUser);
    }

    @Test
    public void testGetAllUsersEmptyList() throws Exception {
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/users")).andExpect(status().isNotFound());
    }

    @Test
    public void testGetAllUsersOk() throws Exception {
        when(userService.createUser(any(User.class))).thenReturn(new HashMap<>());
        when(userService.getAllUsers()).thenReturn(Collections.singletonList(testUser));

        mockMvc.perform(get("/api/users")).andExpect(status().isOk());
    }

    @Test
    public void testGetUserByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/users/1")).andExpect(status().isNotFound())
                .andExpect(jsonPath("message").value("User not found"));
    }

    @Test
    public void testGetUserByIdOk() throws Exception {
        when(userService.getUserById(testUser.getId())).thenReturn(testUser);

        mockMvc.perform(get("/api/users/" + testUser.getId())).andExpect(status().isOk());
    }

    @Test
    public void testCreateUser() throws Exception {
        when(userService.createUser(any(User.class))).thenReturn(new HashMap<>());

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(testUser)))
                .andExpect(status().isCreated());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/invalid_users.csv", numLinesToSkip = 1)
    public void testCreateUserInvalid(String name, String lastName, String email, String username, String password,
            String phone, String expectedField, String expectedMessage) throws Exception {

        when(userService.createUser(any(User.class))).thenReturn(new HashMap<>());

        User invalidUser = new User();
        invalidUser.setName(name);
        invalidUser.setLastName(lastName);
        invalidUser.setEmail(email);
        invalidUser.setUsername(username);
        invalidUser.setPassword(password);
        invalidUser.setPhone(phone);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$." + expectedField).value(expectedMessage));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/unique_users.csv", numLinesToSkip = 1)
    public void tesCreateUserUniqueInvalid(String name, String lastName, String email, String username, String password,
            String phone) throws Exception {

        when(userService.createUser(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("Detail: Unique constraint violation"));

        User invalidUser = new User();
        invalidUser.setName(name);
        invalidUser.setLastName(lastName);
        invalidUser.setEmail(email);
        invalidUser.setUsername(username);
        invalidUser.setPassword(password);
        invalidUser.setPhone(phone);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(invalidUser)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Unique constraint violation"));
    }

    @Test
    public void testUpdateUserNotFound() throws Exception {
        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(testUser)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message").value("User not found"));
    }

    @Test
    public void testUpdateUserOk() throws Exception {
        when(userService.getUserById(testUser.getId())).thenReturn(testUser);
        when(userService.updateUser(testUser)).thenReturn(testUser);

        mockMvc.perform(put("/api/users/" + testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(testUser)))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/invalid_users.csv", numLinesToSkip = 1)
    public void testUpdateUserInvalid(String name, String lastName, String email, String username, String password,
            String phone, String expectedField, String expectedMessage) throws Exception {

        when(userService.getUserById(testUser.getId())).thenReturn(testUser);
        when(userService.updateUser(testUser)).thenReturn(testUser);

        User invalidUser = new User();
        invalidUser.setName(name);
        invalidUser.setLastName(lastName);
        invalidUser.setEmail(email);
        invalidUser.setUsername(username);
        invalidUser.setPassword(password);
        invalidUser.setPhone(phone);

        mockMvc.perform(put("/api/users/" + testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$." + expectedField).value(expectedMessage));
    }

    @Test
    public void testUpdateUserUniqueInvalid() throws Exception {
        when(userService.getUserById(testUser.getId())).thenReturn(testUser);
        when(userService.updateUser(testUser))
                .thenThrow(new DataIntegrityViolationException("Detail: Unique constraint violation"));

        mockMvc.perform(put("/api/users/" + testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(testUser)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Unique constraint violation"));
    }

    @Test
    public void testDeleteUserNotFound() throws Exception {
        mockMvc.perform(delete("/api/users/1")).andExpect(status().isNotFound())
                .andExpect(jsonPath("message").value("User not found"));
    }

    @Test
    public void testDeleteUserOk() throws Exception {
        when(userService.getUserById(testUser.getId())).thenReturn(testUser);
        doNothing().when(userService).deleteUser(testUser.getId());

        mockMvc.perform(delete("/api/users/" + testUser.getId())).andExpect(status().isNoContent());
    }

    @Test
    public void testGet0CardsByUserId() throws Exception {
        when(userService.getUserById(testUser.getId())).thenReturn(testUser);
        when(userService.findCardsByUserId(testUser.getId())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/users/" + testUser.getId() + "/cards")).andExpect(status().isNotFound())
                .andExpect(jsonPath("message").value("No cards found"));
    }

    @Test
    public void testGetCardsByUserId() throws Exception {
        when(userService.getUserById(testUser.getId())).thenReturn(testUser);
        when(userService.findCardsByUserId(testUser.getId())).thenReturn(Collections.singletonList(new Cards()));

        mockMvc.perform(get("/api/users/" + testUser.getId() + "/cards")).andExpect(status().isOk());
    }

    @Test
    public void testNotAddCardsToUserCauseInvalidName() throws Exception {
        when(userService.getUserById(testUser.getId())).thenReturn(testUser);

        Cards card = new Cards();

        UserCards userCards = new UserCards();
        userCards.setCard(card);

        when(userService.addCardToUser(testUser.getId(), card)).thenReturn(userCards);

        mockMvc.perform(post("/api/users/" + testUser.getId() + "/cards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(card)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Name is required"));
    }

    @Test
    public void testAddCardsToUser() throws Exception {
        when(userService.getUserById(testUser.getId())).thenReturn(testUser);

        Cards card = new Cards();
        card.setName("Test Card");

        UserCards userCards = new UserCards();
        userCards.setCard(card);

        when(userService.addCardToUser(testUser.getId(), card)).thenReturn(userCards);

        mockMvc.perform(post("/api/users/" + testUser.getId() + "/cards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(card)))
                .andExpect(status().isOk());
    }

    @Test
    public void testAddCardsToInvalidUser() throws Exception { 
        Cards card = new Cards();
        card.setName("Test Card");

        when(userService.addCardToUser(anyInt(), any(Cards.class))).thenThrow(new IllegalArgumentException());

        mockMvc.perform(post("/api/users/-1/cards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(card)))
                .andExpect(status().isNotFound());
    }

}