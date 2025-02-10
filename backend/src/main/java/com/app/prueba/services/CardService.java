package com.app.prueba.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.prueba.models.Cards;
import com.app.prueba.repositories.CardRepository;

@Service
public class CardService {

    @Autowired
    private CardRepository cardRepository;

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

}
