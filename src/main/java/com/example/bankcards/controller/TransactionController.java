package com.example.bankcards.controller;

import com.example.bankcards.dto.TransactionDTO;
import com.example.bankcards.security.SecurityUtils;
import com.example.bankcards.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@Tag(name = "Transaction Controller", description = "Transaction Management Controller")
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Operation(summary = "Find transaction by ID", description = "Retrieves a specific transaction by its ID")
    @GetMapping("/{id}")
    public TransactionDTO findTransactionById(@NotNull @PathVariable Long id) {
        return transactionService.findById(id);
    }

    @Operation(summary = "Find all transactions for the authenticated user", description = "Retrieves a list of all transactions for the authenticated user")
    @GetMapping("/me")
    public Page<TransactionDTO> findTransactionsForUser(Authentication authentication, Pageable pageable) {
        Long userId = SecurityUtils.getCurrentUserId(authentication);
        return transactionService.findByUserId(userId, pageable);
    }

    @Operation(summary = "Find all transactions for a card", description = "Retrieves a list of all transactions for a specific card")
    @GetMapping("/card/{cardId}")
    public Page<TransactionDTO> findTransactionsForCard(@PathVariable Long cardId, Pageable pageable) {
        return transactionService.findByCardId(cardId, pageable);
    }
}