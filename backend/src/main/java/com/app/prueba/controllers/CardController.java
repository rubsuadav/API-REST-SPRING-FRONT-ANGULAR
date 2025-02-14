package com.app.prueba.controllers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.app.prueba.models.Cards;
import com.app.prueba.services.CardService;
import com.app.prueba.validations.ValidateEntity;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
            return createErrorResponse("No cards found", HttpStatus.NOT_FOUND);
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
            return createErrorResponse("Card not found", HttpStatus.NOT_FOUND);
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
            return createErrorResponse("Card not found", HttpStatus.NOT_FOUND);
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
            return createErrorResponse("Card not found", HttpStatus.NOT_FOUND);
        }
        cardService.deleteCard(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Export card", description = "Export card to json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card exported", content = {
                    @Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description = "Card not found", content = {
                    @Content(mediaType = "application/json") })
    })
    @PostMapping("/export/{id}")
    public ResponseEntity<?> exportCard(@PathVariable int id) throws IOException {
        Cards card = cardService.getCardById(id);
        if (card == null) {
            return createErrorResponse("Card not found", HttpStatus.NOT_FOUND);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            File directory = new File("backend/examples");
            if (!directory.exists()) {
                directory.mkdir();
            }
            objectMapper.writeValue(
                    new File(directory.getAbsolutePath() + "/" + id + "-card-" + card.getName() + ".json"),
                    cardService.exportCardToJSON(id));
        } catch (IOException e) {
            return createErrorResponse("Error creating file", HttpStatus.BAD_REQUEST);
        }
        Map<String, Object> response = new HashMap<>();
        response.put("data", cardService.exportCardToJSON(id));
        response.put("message", "Card exported to " + id + "-card-" + card.getName() + ".json");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Import card", description = "Import card from JSON file")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card imported", content = {
                    @Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description = "Bad request", content = {
                    @Content(mediaType = "application/json") })
    })
    @PostMapping("/import")
    public ResponseEntity<?> importCard(@RequestParam(required = false) MultipartFile file,
            @RequestParam(required = false) Integer userId) {
        if (userId == null) {
            return createErrorResponse("userId is required", HttpStatus.BAD_REQUEST);
        }
        if (validateFile(file) != null) {
            return validateFile(file);
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> cardMap = objectMapper.readValue(file.getInputStream(), new TypeReference<>() {
            });
            Cards card = new Cards();
            card.setName((String) cardMap.get("name"));
            card.setDescription((String) cardMap.get("description"));

            if (!validateCard(card).isEmpty()) {
                return createValidationErrorResponse(validateCard(card));
            }
            return new ResponseEntity<>(cardService.importCardFromJSON(cardMap, userId), HttpStatus.OK);
        } catch (JsonParseException | JsonMappingException e) {
            return createErrorResponse("Invalid JSON format", HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            return createErrorResponse("Error reading file", HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            return createErrorResponse("Invalid userId", HttpStatus.BAD_REQUEST);
        }
    }

    private ResponseEntity<?> validateFile(MultipartFile file) {
        if (file == null) {
            return createErrorResponse("No file uploaded", HttpStatus.BAD_REQUEST);
        }
        if (!file.getOriginalFilename().endsWith(".json")) {
            return createErrorResponse("Invalid file format", HttpStatus.BAD_REQUEST);
        }
        if (file.isEmpty()) {
            return createErrorResponse("File is empty", HttpStatus.BAD_REQUEST);
        }
        return null;
    }

    private ResponseEntity<?> createErrorResponse(String message, HttpStatus status) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", message);
        return new ResponseEntity<>(errorResponse, status);
    }

    private Set<ConstraintViolation<Cards>> validateCard(Cards card) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        return validator.validate(card);
    }

    private ResponseEntity<?> createValidationErrorResponse(
            Set<ConstraintViolation<Cards>> violations) {
        Map<String, String> errorResponse = new HashMap<>();
        for (ConstraintViolation<Cards> violation : violations) {
            errorResponse.put(violation.getPropertyPath().toString(), violation.getMessage());
        }
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
