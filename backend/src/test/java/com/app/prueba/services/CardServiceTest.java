package com.app.prueba.services;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.app.prueba.models.Cards;
import com.app.prueba.repositories.CardRepository;
import com.app.prueba.utils.Utils;

@SpringBootTest
@ActiveProfiles("test")
public class CardServiceTest {

    @Autowired
    private CardService cardService;

    @Autowired
    private CardRepository cardRepository;

    private Cards testCard;

    @Autowired
    private Utils utils;

    @BeforeEach
    public void setUp() {
        cardRepository.deleteAll();

        testCard = utils.createCard("Test Card", "Test Card Description");
        cardRepository.save(testCard);
    }

    @Test
    @DisplayName("Test - Get all cards")
    public void testGetAllCards() {
        assertEquals(1, cardService.getAllCards().size());
    }

    @Test
    @DisplayName("Test - Get card by id")
    public void testGetCardById() {
        Cards card = cardService.getCardById(testCard.getId());
        assertNotNull(card);
        assertEquals(testCard.getId(), card.getId());
    }

    @Test
    @DisplayName("Test - Can`t get the card by id (NOT FOUND)")
    public void testGetCardByIdNotFound() {
        assertNull(cardService.getCardById(2));
    }

    @Test
    @DisplayName("Test - Can`t get the card by id (INVALID ID)")
    public void testGetCardByIdInvalidId() {
        assertNull(cardService.getCardById(0));
        assertNull(cardService.getCardById(-2));
    }

    @Test
    @DisplayName("Test - Create card")
    public void testCreateCard() {
        Cards newCard = utils.createCard("New Card", "New Card Description");

        assertNotNull(cardService.createCard(newCard));
        assertEquals(2, cardService.getAllCards().size());
    }

    @Test
    @DisplayName("Test - Update card")
    public void testUpdateCard() {
        testCard.setName("Updated");
        testCard.setDescription("Updated Description");

        Cards updatedUser = cardService.updateCard(testCard);
        assertNotNull(updatedUser);
        assertEquals("Updated", updatedUser.getName());
    }

    @Test
    @DisplayName("Test - Delete card")
    public void testDeleteCard() {
        cardService.deleteCard(testCard.getId());
        assertEquals(0, cardService.getAllCards().size());
    }

}