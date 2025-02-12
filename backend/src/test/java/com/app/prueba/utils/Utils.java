package com.app.prueba.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.app.prueba.models.Cards;
import com.app.prueba.models.User;

@Component
public class Utils {

    public User createUser(String name, String lastName, String email, String username, String password,
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

    public Cards createCard(String name, String description) {
        Cards card = new Cards();
        card.setName(name);
        card.setDescription(description);
        return card;
    }

    public Map<String, Object> createCardMap(String name, String description) {
        Map<String, Object> card = new HashMap<>();
        card.put("name", name);
        card.put("description", description);
        return card;
    }

}
