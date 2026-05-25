package com.example.bankcards.controller;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

import static org.assertj.core.api.Assertions.assertThat;

class RestExceptionHandlerTest {

    private final RestExceptionHandler handler = new RestExceptionHandler();

    @Test
    void handleNotFound_returns404() {
        ResponseEntity<Object> response = handler.handleNotFound(new ResourceNotFoundException("missing"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("missing");
    }

    @Test
    void handleBadRequest_illegalArgument_returns400() {
        ResponseEntity<Object> response = handler.handleBadRequest(new IllegalArgumentException("bad input"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("bad input");
    }

    @Test
    void handleBadRequest_constraintViolation_returns400() {
        ResponseEntity<Object> response = handler.handleBadRequest(new ConstraintViolationException("violation", null));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void handleConflict_dataIntegrity_returns409() {
        ResponseEntity<Object> response = handler.handleConflict(new DataIntegrityViolationException("conflict"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isEqualTo("conflict");
    }

    @Test
    void handleAccessDenied_returns403() {
        ResponseEntity<Object> response = handler.handleAccessDenied(new AccessDeniedException("denied"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().toString()).contains("denied");
    }

    @Test
    void handleAuthentication_returns401() {
        AuthenticationException ex = new AuthenticationException("auth failed") {};

        ResponseEntity<Object> response = handler.handleAuthentication(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().toString()).contains("auth failed");
    }

    @Test
    void handleGenericException_returns500() {
        ResponseEntity<Object> response = handler.handleGenericException(new RuntimeException("oops"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().toString()).contains("oops");
    }
}
