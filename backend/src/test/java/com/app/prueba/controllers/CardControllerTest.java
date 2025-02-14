package com.app.prueba.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.io.IOException;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.app.prueba.models.Cards;
import com.app.prueba.models.User;
import com.app.prueba.services.CardService;
import com.app.prueba.services.UserService;
import com.app.prueba.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CardController.class)
public class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private Utils utils;

    @MockitoBean
    private CardService cardService;

    @MockitoBean
    private UserService userService;

    @InjectMocks
    private CardController cardController;

    private Cards testCard;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(cardController).build();

        testCard = new Cards();
        testCard.setId(1);
        testCard.setName("Test");
        testCard.setDescription("Test Description");

        when(utils.createCard("Test", "Test Description")).thenReturn(testCard);

    }

    @Test
    public void testGetAllCardsEmptyList() throws Exception {
        when(cardService.getAllCards()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/cards")).andExpect(status().isNotFound());
    }

    @Test
    public void testGetAllCardsOk() throws Exception {
        when(cardService.createCard(any(Cards.class))).thenReturn(new HashMap<>());
        when(cardService.getAllCards()).thenReturn(Collections.singletonList(testCard));

        mockMvc.perform(get("/api/cards")).andExpect(status().isOk());
    }

    @Test
    public void testGetCardByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/cards/1")).andExpect(status().isNotFound())
                .andExpect(jsonPath("message").value("Card not found"));
    }

    @Test
    public void testGetCardByIdOk() throws Exception {
        when(cardService.getCardById(testCard.getId())).thenReturn(testCard);

        mockMvc.perform(get("/api/cards/" + testCard.getId())).andExpect(status().isOk());
    }

    @Test
    public void testCreateCard() throws Exception {
        when(cardService.createCard(any(Cards.class))).thenReturn(new HashMap<>());

        mockMvc.perform(post("/api/cards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(testCard)))
                .andExpect(status().isCreated());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/invalid_cards.csv", numLinesToSkip = 1)
    public void testCreateCardInvalid(String name, String expectedField, String expectedMessage) throws Exception {

        when(cardService.createCard(any(Cards.class))).thenReturn(new HashMap<>());

        Cards invalidCard = new Cards();
        invalidCard.setName(name);
        invalidCard.setDescription("description");

        mockMvc.perform(post("/api/cards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(invalidCard)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$." + expectedField).value(expectedMessage));
    }

    @Test
    public void testUpdateCardNotFound() throws Exception {
        mockMvc.perform(put("/api/cards/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(testCard)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message").value("Card not found"));
    }

    @Test
    public void testUpdateCardOk() throws Exception {
        when(cardService.getCardById(testCard.getId())).thenReturn(testCard);
        when(cardService.updateCard(testCard)).thenReturn(testCard);

        mockMvc.perform(put("/api/cards/" + testCard.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(testCard)))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/invalid_cards.csv", numLinesToSkip = 1)
    public void testUpdateCardInvalid(String name, String expectedField, String expectedMessage) throws Exception {

        when(cardService.getCardById(testCard.getId())).thenReturn(testCard);
        when(cardService.updateCard(testCard)).thenReturn(testCard);

        Cards invalidCard = new Cards();
        invalidCard.setName(name);
        invalidCard.setDescription("description");

        mockMvc.perform(put("/api/cards/" + testCard.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(invalidCard)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$." + expectedField).value(expectedMessage));
    }

    @Test
    public void testDeleteUserNotFound() throws Exception {
        mockMvc.perform(delete("/api/cards/1")).andExpect(status().isNotFound())
                .andExpect(jsonPath("message").value("Card not found"));
    }

    @Test
    public void testDeleteUserOk() throws Exception {
        when(cardService.getCardById(testCard.getId())).thenReturn(testCard);
        doNothing().when(cardService).deleteCard(testCard.getId());

        mockMvc.perform(delete("/api/cards/" + testCard.getId())).andExpect(status().isNoContent());
    }

    @Test
    public void testExportCardNotFound() throws Exception {
        mockMvc.perform(post("/api/cards/export/1")).andExpect(status().isNotFound())
                .andExpect(jsonPath("message").value("Card not found"));
    }

    @Test
    public void testExportCardBadFile() throws Exception {
        when(cardService.getCardById(testCard.getId())).thenReturn(testCard);
        when(cardService.exportCardToJSON(testCard.getId())).thenThrow(new IOException());

        mockMvc.perform(post("/api/cards/export/" + testCard.getId()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value("Error creating file"));
    }

    @Test
    public void testExportCardOk() throws Exception {
        when(cardService.getCardById(testCard.getId())).thenReturn(testCard);
        when(cardService.exportCardToJSON(testCard.getId())).thenReturn(new HashMap<>());

        mockMvc.perform(post("/api/cards/export/" + testCard.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message")
                        .value("Card exported to " + testCard.getId() + "-card-" + testCard.getName() + ".json"))
                .andExpect(jsonPath("data").exists());
    }

    @Test
    public void testImportCardNoUserProvided() throws Exception {
        mockMvc.perform(post("/api/cards/import")).andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value("userId is required"));
    }

    @Test
    public void testImportCardInvalidUserId() throws Exception {
        User testUser = new User();

        MockMultipartFile validFile = new MockMultipartFile("file", "valid.json", "application/json",
                "{\"name\":\"Test\",\"description\":\"Test Description\"}".getBytes());

        when(cardService.importCardFromJSON(anyMap(), anyInt()))
                .thenThrow(new IllegalArgumentException());

        mockMvc.perform(multipart(HttpMethod.POST, "/api/cards/import?userId=" + testUser.getId())
                .file(validFile))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value("Invalid userId"));
    }

    @Test
    public void testImportCardNoFileProvided() throws Exception {
        User testUser = new User();

        when(userService.createUser(any(User.class))).thenReturn(new HashMap<>());
        when(userService.getUserById(testUser.getId())).thenReturn(testUser);

        mockMvc.perform(post("/api/cards/import?userId=" + testUser.getId())).andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value("No file uploaded"));
    }

    @Test
    public void testImportCardInvalidFile() throws Exception {
        User testUser = new User();

        MockMultipartFile invalidFile = new MockMultipartFile("file", "invalid.txt", "text/plain",
                "invalid content".getBytes());

        when(userService.getUserById(testUser.getId())).thenReturn(testUser);

        mockMvc.perform(multipart(HttpMethod.POST, "/api/cards/import?userId=" + testUser.getId())
                .file(invalidFile))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value("Invalid file format"));
    }

    @Test
    public void testImportCardValidEmptyFile() throws Exception {
        User testUser = new User();

        MockMultipartFile invalidFile = new MockMultipartFile("file", "empty.json", "application/json",
                "".getBytes());

        when(userService.getUserById(testUser.getId())).thenReturn(testUser);

        mockMvc.perform(multipart(HttpMethod.POST, "/api/cards/import?userId=" + testUser.getId())
                .file(invalidFile))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value("File is empty"));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/invalid_imported_cards.csv", numLinesToSkip = 1)
    public void testImportCardInvalidData(String name, String expectedField, String expectedMessage) throws Exception {
        User testUser = new User();

        MockMultipartFile invalidFile = new MockMultipartFile("file", "invalid.json", "application/json",
                ("{\"name\":\"" + name + "\",\"description\":\"Test Description\"}").getBytes());

        when(userService.getUserById(testUser.getId())).thenReturn(testUser);

        mockMvc.perform(multipart(HttpMethod.POST, "/api/cards/import?userId=" + testUser.getId())
                .file(invalidFile))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(expectedField).value(expectedMessage));
    }

    @Test
    public void testImportCardValidFileAndValidData() throws Exception {
        User testUser = new User();

        MockMultipartFile validFile = new MockMultipartFile("file", "valid.json", "application/json",
                "{\"name\":\"Test\",\"description\":\"Test Description\"}".getBytes());

        when(userService.getUserById(testUser.getId())).thenReturn(testUser);
        when(cardService.createCard(any(Cards.class))).thenReturn(new HashMap<>());

        mockMvc.perform(multipart(HttpMethod.POST, "/api/cards/import?userId=" + testUser.getId())
                .file(validFile))
                .andExpect(status().isOk());
    }

}