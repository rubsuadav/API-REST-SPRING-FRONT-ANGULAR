package com.app.prueba.services;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.prueba.models.User;
import com.app.prueba.repositories.UserRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public Map<String, Object> register(User user) {
        userRepository.save(user);

        String token = generateToken(user);
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("userId", user.getId());
        response.put("isValid", validateToken(token));

        return response;
    }

    public Map<String, Object> login(User user) {
        Map<String, Object> response = new HashMap<>();
        User userFound = userRepository.findUserByUsernameOrEmail(user.getUsername(), user.getEmail());
        if (userFound == null) {
            response.put("message", "Username or email not found");
            return response;
        }
        if (!userFound.getPassword().equals(user.getPassword())) {
            response.put("message", "Invalid password");
            return response;
        }

        String token = generateToken(userFound);
        response.put("token", token);
        response.put("userId", userFound.getId());
        response.put("isValid", validateToken(token));

        return response;
    }

    private String generateToken(User user) {
        return Jwts.builder().subject(user.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 1 día
                .signWith(getSigningKey()).compact();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor("your_256_bit_secret_key_your_256_bit_secret_key".getBytes(StandardCharsets.UTF_8));
    }

    private boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
