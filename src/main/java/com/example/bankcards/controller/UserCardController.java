package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.security.SecurityUtils;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@Validated
@Tag(name = "Card Controller", description = "Card Management Controller For Users")
@RequestMapping("/api/v1/users/me/cards")
@PreAuthorize("hasRole('USER')")
public class UserCardController {

    private final CardService cardService;

    public UserCardController(CardService cardService) {
        this.cardService = cardService;
    }

    @Operation(summary = "Get cards by user id", description = "Retrieve a list of cards for a specific user")
    @GetMapping
    public Page<CardDTO> getCardsForUser(Authentication authentication, Pageable pageable) {
        Long userId = SecurityUtils.getCurrentUserId(authentication);
        return cardService.findByUserId(userId, pageable);
    }

    @Operation(summary = "Search cards", description = "Retrieve a list of cards by pan for a specific user")
    @GetMapping("/search")
    public Page<CardDTO> searchCardsForUser(Authentication authentication, @RequestParam(name = "query") String query, Pageable pageable) {
        Long userId = SecurityUtils.getCurrentUserId(authentication);
        return cardService.search(userId, query, pageable);
    }

    @Operation(summary = "Transfer balance", description = "Transfer balance from one card to another")
    @PostMapping("/{cardId}/transfers")
    public void transferBalance(Authentication authentication, @PathVariable Long cardId,
                                @RequestParam(name = "targetCardId") Long targetCardId,
                                @Valid @RequestBody TransferRequest request) {
        Long userId = SecurityUtils.getCurrentUserId(authentication);
        cardService.transferBalance(userId, cardId, targetCardId, request.amount());
    }

    @Operation(summary = "Request a card block", description = "User request for block a specific card")
    @PatchMapping("/{cardId}")
    public void requestCardBlock(Authentication authentication, @PathVariable Long cardId) {
        Long userId = SecurityUtils.getCurrentUserId(authentication);
        cardService.requestCardBlock(userId, cardId);
    }

    @Operation(summary = "Get balance", description = "Get balance for a specific card")
    @GetMapping("/{cardId}/balance")
    public BigDecimal getBalance(Authentication authentication, @PathVariable Long cardId) {
        Long userId = SecurityUtils.getCurrentUserId(authentication);
        return cardService.getBalance(userId, cardId);
    }
}