package com.app.prueba.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.prueba.models.Cards;
import com.app.prueba.models.User;
import com.app.prueba.models.UserCards;
import com.app.prueba.repositories.CardRepository;
import com.app.prueba.repositories.UserCardsRepository;
import com.app.prueba.repositories.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private UserCardsRepository userCardsRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Integer id) {
        if (!(id instanceof Integer)) {
            return null;
        } else if (id < 0) {
            return null;
        } else if (id == 0) {
            return null;
        }

        if (userRepository.existsById(id)) {
            return userRepository.findById(id).get();

        }
        return null;
    }

    public Map<String, Object> createUser(User user) {
        userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("user", user);

        return response;
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }

    public List<Cards> findCardsByUserId(int userId) {
        return cardRepository.findCardsByUserId(userId);
    }

    public User getUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    public User findUserByEmailAndPassword(String email, String password) {
        return userRepository.findUserByEmailAndPassword(email, password);
    }

    public UserCards addCardToUser(int userId, Cards card) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

        Cards saveCard = cardRepository.save(card);

        UserCards userCards = new UserCards();
        userCards.setUser(user);
        userCards.setCard(saveCard);

        return userCardsRepository.save(userCards);
    }
}
