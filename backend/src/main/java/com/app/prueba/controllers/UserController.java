package com.app.prueba.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import com.app.prueba.models.Cards;
import com.app.prueba.models.User;
import com.app.prueba.models.UserCards;
import com.app.prueba.services.UserService;
import com.app.prueba.validations.ValidateEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("api/users")
@Tag(name = "Users", description = "The User API")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Get all users", description = "Get all users from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = User.class, type = "array")) }),
            @ApiResponse(responseCode = "404", description = "No users found", content = {
                    @Content(mediaType = "application/json") })
    })
    @GetMapping
    public ResponseEntity<?> getUsers() {
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "No users found");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @Operation(summary = "Get user by id", description = "Get a user by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)) }),
            @ApiResponse(responseCode = "404", description = "User not found", content = {
                    @Content(mediaType = "application/json") })
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable int id) {
        User user = userService.getUserById(id);
        if (user == null) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "User not found");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Operation(summary = "Create a new user", description = "Create a new user in the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)) }),
            @ApiResponse(responseCode = "409", description = "Conflict", content = {
                    @Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description = "Bad request", content = {
                    @Content(mediaType = "application/json") })
    })
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody User user, BindingResult bindingResult) {
        ValidateEntity uniqueUser = new ValidateEntity();
        if (uniqueUser.getErrorResponse(bindingResult) != null) {
            return uniqueUser.getErrorResponse(bindingResult);
        }
        try {
            return new ResponseEntity<>(userService.createUser(user), HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMostSpecificCause().getMessage().split("Detail: ")[1]);
            return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
        }
    }

    @Operation(summary = "Update a user", description = "Update a user in the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)) }),
            @ApiResponse(responseCode = "404", description = "User not found", content = {
                    @Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "409", description = "Conflict", content = {
                    @Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description = "Bad request", content = {
                    @Content(mediaType = "application/json") })
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable int id, @Valid @RequestBody User user,
            BindingResult bindingResult) {
        User userToUpdate = userService.getUserById(id);
        if (userToUpdate == null) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "User not found");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        ValidateEntity uniqueUser = new ValidateEntity();
        if (uniqueUser.getErrorResponse(bindingResult) != null) {
            return uniqueUser.getErrorResponse(bindingResult);
        }
        try {
            userToUpdate.setName(user.getName());
            userToUpdate.setLastName(user.getLastName());
            userToUpdate.setEmail(user.getEmail());
            userToUpdate.setPassword(user.getPassword());
            userToUpdate.setPhone(user.getPhone());
            userToUpdate.setUsername(user.getUsername());
            return new ResponseEntity<>(userService.updateUser(userToUpdate), HttpStatus.OK);
        } catch (DataIntegrityViolationException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMostSpecificCause().getMessage().split("Detail: ")[1]);
            return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
        }

    }

    @Operation(summary = "Delete a user", description = "Delete a user from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted", content = {
                    @Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description = "User not found", content = {
                    @Content(mediaType = "application/json") })
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable int id) {
        User user = userService.getUserById(id);
        if (user == null) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "User not found");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Get cards by user id", description = "Get all cards from a user by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cards found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Cards.class, type = "array")) }),
            @ApiResponse(responseCode = "404", description = "No cards found", content = {
                    @Content(mediaType = "application/json") })
    })
    @GetMapping("/{id}/cards")
    public ResponseEntity<?> getCardsByUserId(@PathVariable int id) {
        List<Cards> cards = userService.findCardsByUserId(id);
        if (cards.isEmpty()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "No cards found");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(cards, HttpStatus.OK);
    }

    @Operation(summary = "Add card to user", description = "Add a card to a user by their IDs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card added to user", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserCards.class)) }),
            @ApiResponse(responseCode = "404", description = "User or card not found", content = {
                    @Content(mediaType = "application/json") })
    })
    @PostMapping("/{userId}/cards")
    public ResponseEntity<?> addCardToUser(@PathVariable int userId, @Valid @RequestBody Cards card,
            BindingResult bindingResult) {
        ValidateEntity uniqueCard = new ValidateEntity();
        if (uniqueCard.getErrorResponse(bindingResult) != null) {
            return uniqueCard.getErrorResponse(bindingResult);
        }
        try {
            UserCards userCards = userService.addCardToUser(userId, card);
            return new ResponseEntity<>(userCards, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }
}