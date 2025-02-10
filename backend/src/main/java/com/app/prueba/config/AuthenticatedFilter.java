package com.app.prueba.config;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthenticatedFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        Boolean isGetRequest = request.getMethod().equals("GET");
        Boolean isPostRequest = request.getMethod().equals("POST");
        Boolean isPutRequest = request.getMethod().equals("PUT");
        Boolean isDeleteRequest = request.getMethod().equals("DELETE");

        Boolean isGetCardsByUserIdRequest = request.getRequestURI().matches("/api/users/\\d+/cards");
        Boolean isAuthRequest = request.getRequestURI().startsWith("/api/auth/");

        if ((authorizationHeader == null || authorizationHeader.isEmpty()) &&
                (!isGetRequest || isGetCardsByUserIdRequest || isPostRequest || isPutRequest || isDeleteRequest) &&
                !isAuthRequest) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            PrintWriter writer = response.getWriter();
            writer.write("{\"message\": \"You must be logged in\"}");
            writer.flush();
            writer.close();
            return;
        }
        filterChain.doFilter(request, response);
    }

}
