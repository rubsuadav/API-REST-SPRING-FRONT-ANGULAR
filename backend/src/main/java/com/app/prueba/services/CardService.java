package com.app.prueba.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.prueba.models.Cards;
import com.app.prueba.models.UserCards;
import com.app.prueba.repositories.CardRepository;
import com.app.prueba.repositories.UserCardsRepository;
import com.app.prueba.repositories.UserRepository;

@Service
public class CardService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private UserCardsRepository userCardsRepository;

    public List<Cards> getAllCards() {
        return cardRepository.findAll();
    }

    public Cards getCardById(Integer id) {
        if (!(id instanceof Integer)) {
            return null;
        } else if (id < 0) {
            return null;
        } else if (id == 0) {
            return null;
        }

        if (cardRepository.existsById(id)) {
            return cardRepository.findById(id).get();

        }
        return null;
    }

    public Map<String, Object> createCard(Cards card) {
        cardRepository.save(card);

        Map<String, Object> response = new HashMap<>();
        response.put("card", card);

        return response;
    }

    public Cards updateCard(Cards card) {
        return cardRepository.save(card);
    }

    public void deleteCard(int id) {
        cardRepository.deleteById(id);
    }

    public Map<String, Object> exportCardToJSON(int id) throws IOException {
        Cards card = cardRepository.findById(id).get();

        Map<String, Object> response = new HashMap<>();
        response.put("id", card.getId());
        response.put("name", card.getName());
        response.put("description", card.getDescription());

        return response;
    }

    public Map<String, Object> importCardFromJSON(Map<String, Object> cardMap, int userId) {
        if (userRepository.existsById(userId)) {
            UserCards userCards = new UserCards();
            userCards.setUser(userRepository.findById(userId).get());

            Cards card = new Cards();
            card.setName((String) cardMap.get("name"));
            card.setDescription((String) cardMap.get("description"));

            cardRepository.save(card);

            userCards.setCard(card);
            userCardsRepository.save(userCards);

            Map<String, Object> response = new HashMap<>();
            response.put("card", card);
            response.put("user", userCards.getUser().getUsername());

            return response;
        }
        throw new IllegalArgumentException("User not found");
    }
}
