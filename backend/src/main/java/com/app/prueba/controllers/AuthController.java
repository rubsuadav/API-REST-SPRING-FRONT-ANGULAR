package com.app.prueba.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import com.app.prueba.models.User;
import com.app.prueba.services.AuthService;
import com.app.prueba.validations.ValidateEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("api/auth")
@Tag(name = "Authentication", description = "The Authentication API based on JWT")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Operation(summary = "Register a new user", description = "Register a new user in the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)) }),
            @ApiResponse(responseCode = "409", description = "Conflict", content = {
                    @Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description = "Bad request", content = {
                    @Content(mediaType = "application/json") })
    })
    @PostMapping("register")
    public ResponseEntity<?> register(@Valid @RequestBody User user, BindingResult bindingResult) {
        ValidateEntity uniqueUser = new ValidateEntity();
        if (uniqueUser.getErrorResponse(bindingResult) != null) {
            return uniqueUser.getErrorResponse(bindingResult);
        }
        try {
            return new ResponseEntity<>(authService.register(user), HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMostSpecificCause().getMessage().split("Detail: ")[1]);
            return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
        }
    }

    @Operation(summary = "Login", description = "Login with the user credentials")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User logged in", content = {
                    @Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {
                    @Content(mediaType = "application/json") })
    })
    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody User user) {
        Map<String, Object> response = authService.login(user);
        if (response.containsKey("message")) {
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
