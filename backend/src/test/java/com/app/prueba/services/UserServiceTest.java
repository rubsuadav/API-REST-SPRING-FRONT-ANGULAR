package com.app.prueba.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.app.prueba.models.Cards;
import com.app.prueba.models.User;
import com.app.prueba.repositories.CardRepository;
import com.app.prueba.repositories.UserRepository;
import com.app.prueba.utils.Utils;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CardRepository cardRepository;

    private User testUser;
    private Cards testCard;

    @Autowired
    private Utils utils;

    @BeforeEach
    public void setUp() {
        cardRepository.deleteAll();
        userRepository.deleteAll();

        testUser = utils.createUser("Test", "User", "testuser@gmail.com", "testuser", "password",
                "1234567890");
        userRepository.save(testUser);

        testCard = utils.createCard("Test Card", "Test Card Description");
        cardRepository.save(testCard);
    }

    @Test
    @DisplayName("Test - Get all users")
    public void testGetAllUsers() {
        assertEquals(1, userService.getAllUsers().size());
    }

    @Test
    @DisplayName("Test - Get user by id")
    public void testGetUserById() {
        User user = userService.getUserById(testUser.getId());
        assertNotNull(user);
        assertEquals(testUser.getId(), user.getId());
    }

    @Test
    @DisplayName("Test - Can`t get the user by id (NOT FOUND)")
    public void testGetUserByIdNotFound() {
        assertNull(userService.getUserById(2));
    }

    @Test
    @DisplayName("Test - Can`t get the user by id (INVALID id)")
    public void testGetUserByIdInvalidId() {
        assertNull(userService.getUserById(0));
        assertNull(userService.getUserById(-2));
    }

    @Test
    @DisplayName("Test - Create user")
    public void testCreateUser() {
        User newUser = utils.createUser("New", "User", "newtestuser@yahoo.com", "newuser", "password", "0987654321");

        assertNotNull(userService.createUser(newUser));
        assertEquals(2, userService.getAllUsers().size());
    }

    @Test
    @DisplayName("Test - Update user")
    public void testUpdateUser() {
        testUser.setName("Updated");
        testUser.setLastName("User");
        testUser.setEmail("updated@gmail.com");
        testUser.setUsername("updateduser");
        testUser.setPassword("updatedpassword");
        testUser.setPhone("0987654321");

        User updatedUser = userService.updateUser(testUser);
        assertNotNull(updatedUser);
        assertEquals("Updated", updatedUser.getName());
    }

    @Test
    @DisplayName("Test - Delete user")
    public void testDeleteUser() {
        userService.deleteUser(testUser.getId());
        assertEquals(0, userService.getAllUsers().size());
    }

    @Test
    @DisplayName("Test - Find cards by user id")
    public void testFindCardsByUserId() {
        assertTrue(userService.findCardsByUserId(testUser.getId()) instanceof List);
        assertEquals(0, userService.findCardsByUserId(testUser.getId()).size());
    }

    @Test
    @DisplayName("Test - Get user by email")
    public void testGetUserByEmail() {
        User user = userService.getUserByEmail(testUser.getEmail());
        assertNotNull(user);
        assertEquals(testUser.getEmail(), user.getEmail());
    }

    @Test
    @DisplayName("Test - Find user by email and password")
    public void testFindUserByEmailAndPassword() {
        User user = userService.findUserByEmailAndPassword(testUser.getEmail(), testUser.getPassword());
        assertNotNull(user);
        assertEquals(testUser.getEmail(), user.getEmail());
    }
}