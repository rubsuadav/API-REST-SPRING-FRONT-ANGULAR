package com.app.prueba.controllers;

import static org.mockito.ArgumentMatchers.any;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.app.prueba.models.Cards;
import com.app.prueba.services.CardService;
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

}