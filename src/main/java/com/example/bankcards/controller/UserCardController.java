package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.service.CardManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;


@RestController
@Validated
@Tag(name = "Card Controller", description = "Card Management Controller For Users")
@RequestMapping("/api/v1/users/{userId}/cards")
@PreAuthorize("hasRole('USER')")
public class UserCardController {

    CardManager cardManager;

    @Autowired
    public UserCardController(CardManager cardManager) {
        this.cardManager = cardManager;
    }

    @Operation(summary = "Get cards by user id", description = "Retrieve a list of cards for a specific user")
    @GetMapping()
    public Page<CardDTO> getCardsForUser(@PathVariable Long userId, Pageable pageable) {
        return cardManager.findByUserId(userId, pageable);
    }

    @Operation(summary = "Search cards", description = "Retrieve a list of cards by pan for a specific user")
    @GetMapping("/search")
    public Page<CardDTO> searchCardsForUser(@PathVariable Long userId, @RequestParam(name = "query") String query, Pageable pageable) {
        return cardManager.search(userId, query, pageable);
    }

    @Operation(summary = "Request a card block", description = "User request for block a specific card")
    @PatchMapping("/{cardId}")
    public void requestCardBlock(@PathVariable Long userId, @PathVariable Long cardId) {
        cardManager.requestCardBlock(userId, cardId);
    }

    @Operation(summary = "Get balance", description = "Get balance for a specific card")
    @GetMapping("/{cardId}/balance")
    public BigDecimal getBalance(@PathVariable Long userId, @PathVariable Long cardId) {
        return cardManager.getBalance(userId, cardId);
    }
}
