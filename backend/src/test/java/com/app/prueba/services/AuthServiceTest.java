package com.app.prueba.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.app.prueba.models.User;
import com.app.prueba.repositories.UserRepository;
import com.app.prueba.utils.Utils;

@SpringBootTest
@ActiveProfiles("test")
public class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @Autowired
    private Utils utils;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll(); // Limpiar la base de datos antes de cada prueba

        testUser = utils.createUser("Test", "User", "testuser@gmail.com", "testuser", "password",
                "1234567890");
        userRepository.save(testUser);
    }

    @Test
    @DisplayName("Test de registro de usuario")
    public void testRegister() {
        User newUser = utils.createUser("New", "User", "newuser@gmail.com", "newuser", "newpassword",
                "0987654321");

        Map<String, Object> response = authService.register(newUser);

        assertNotNull(response.get("token"));
        assertNotNull(response.get("userId"));
        assertTrue((Boolean) response.get("isValid"));
    }

    @Test
    @DisplayName("Test de inicio de sesión")
    public void testLogin() {
        Map<String, Object> response = authService.login(testUser);

        assertNotNull(response.get("token"));
        assertNotNull(response.get("userId"));
        assertTrue((Boolean) response.get("isValid"));
    }

    @Test
    @DisplayName("Test de inicio de sesión con nombre de usuario correcto y contraseña incorrecta")
    public void testLoginInvalidPassword() {
        User invalidUser = new User();
        invalidUser.setUsername("testuser");
        invalidUser.setEmail("testuser@gmail.com");
        invalidUser.setPassword("wrongpassword");

        Map<String, Object> response = authService.login(invalidUser);

        assertEquals("Invalid password", response.get("message"));
    }

    @Test
    @DisplayName("Test de inicio de sesión con nombre de usuario incorrecto")
    public void testLoginUserNotFound() {
        User nonExistentUser = new User();
        nonExistentUser.setUsername("nonexistent");
        nonExistentUser.setEmail("nonexistent@gmail.com");
        nonExistentUser.setPassword("password");

        Map<String, Object> response = authService.login(nonExistentUser);

        assertEquals("Username or email not found", response.get("message"));
    }
}