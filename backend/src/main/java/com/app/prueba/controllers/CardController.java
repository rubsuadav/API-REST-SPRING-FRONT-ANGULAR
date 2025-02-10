package com.app.prueba.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import com.app.prueba.models.Cards;
import com.app.prueba.services.CardService;
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
@RequestMapping("api/cards")
@Tag(name = "Cards", description = "CRUD operations for cards")
public class CardController {

    @Autowired
    private CardService cardService;

    @Operation(summary = "Get all cards", description = "Get all cards from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cards found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Cards.class, type = "array")) }),
            @ApiResponse(responseCode = "404", description = "No cards found", content = {
                    @Content(mediaType = "application/json") })
    })
    @GetMapping
    public ResponseEntity<?> getCards() {
        List<Cards> cards = cardService.getAllCards();
        if (cards.isEmpty()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "No cards found");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(cards, HttpStatus.OK);
    }

    @Operation(summary = "Get card by id", description = "Get card by id from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Cards.class)) }),
            @ApiResponse(responseCode = "404", description = "Card not found", content = {
                    @Content(mediaType = "application/json") })
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getCardById(@PathVariable Integer id) {
        Cards card = cardService.getCardById(id);
        if (card == null) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Card not found");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(card, HttpStatus.OK);
    }

    @Operation(summary = "Create card", description = "Create card in the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Card created", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Cards.class)) }),
            @ApiResponse(responseCode = "409", description = "Conflict", content = {
                    @Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description = "Bad request", content = {
                    @Content(mediaType = "application/json") })
    })
    @PostMapping
    public ResponseEntity<?> createCard(@Valid @RequestBody Cards card, BindingResult bindingResult) {
        ValidateEntity uniqueCard = new ValidateEntity();
        if (uniqueCard.getErrorResponse(bindingResult) != null) {
            return uniqueCard.getErrorResponse(bindingResult);
        }
        return new ResponseEntity<>(cardService.createCard(card), HttpStatus.CREATED);
    }

    @Operation(summary = "Update card", description = "Update card in the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card updated", content = @Content(schema = @Schema(implementation = Cards.class))),
            @ApiResponse(responseCode = "400", description = "Invalid card", content = @Content(schema = @Schema(implementation = Cards.class))),
            @ApiResponse(responseCode = "404", description = "Card not found", content = @Content(schema = @Schema(implementation = Cards.class))) })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCard(@PathVariable int id, @Valid @RequestBody Cards card,
            BindingResult bindingResult) {
        Cards cardToUpdate = cardService.getCardById(id);
        if (cardToUpdate == null) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Card not found");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        ValidateEntity uniqueCard = new ValidateEntity();
        if (uniqueCard.getErrorResponse(bindingResult) != null) {
            return uniqueCard.getErrorResponse(bindingResult);
        }
        cardToUpdate.setName(card.getName());
        cardToUpdate.setDescription(card.getDescription());
        return new ResponseEntity<>(cardService.updateCard(cardToUpdate), HttpStatus.OK);
    }

    @Operation(summary = "Delete card", description = "Delete card from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Card deleted", content = {
                    @Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description = "Card not found", content = {
                    @Content(mediaType = "application/json") })
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCard(@PathVariable int id) {
        Cards card = cardService.getCardById(id);
        if (card == null) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Card not found");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        cardService.deleteCard(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
