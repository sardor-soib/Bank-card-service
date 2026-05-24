package com.example.bankcards.controller;

import com.example.bankcards.dto.TransactionDTO;
import com.example.bankcards.service.TransactionManager;
import com.example.bankcards.service.impl.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@Tag(name = "Transaction Controller", description = "Transaction Management Controller")
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionManager transactionManager;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionManager = transactionService;
    }

    @Operation(summary = "Find transaction by ID", description = "Retrieves a specific transaction by its ID")
    @GetMapping("/{id}")
    public TransactionDTO findTransactionById(@NotNull @PathVariable Long id) {
        return transactionManager.findById(id);
    }

    @Operation(summary = "Find all transactions for a user", description = "Retrieves a list of all transactions for a specific user")
    @GetMapping("/user/{userId}")
    public Page<TransactionDTO> findTransactionsForUser(@NotNull @PathVariable Long userId, Pageable pageable) {
        return transactionManager.findAll(pageable);
    }

    @Operation(summary = "Find all transactions for a card", description = "Retrieves a list of all transactions for a specific card")
    @GetMapping("/card/{cardId}")
    public Page<TransactionDTO> findTransactionsForCard(@PathVariable Long cardId, Pageable pageable) {
        return transactionManager.findByCardId(cardId, pageable);
    }

    @Operation(summary = "Create a new transaction", description = "Creates a new transaction for a specific card")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionDTO createTransaction(@NotNull @Validated TransactionDTO transactionDTO) {
        return transactionManager.create(transactionDTO);
    }

    @Operation(summary = "Update an existing transaction", description = "Updates an existing transaction by its ID")
    @PutMapping
    public TransactionDTO updateTransaction(@NotNull Long id, @NotNull TransactionDTO transactionDTO) {
        return transactionManager.update(id, transactionDTO);
    }

    @Operation(summary = "Delete a transaction", description = "Deletes a specific transaction by its ID")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTransaction(@NotNull @PathVariable Long id) {
        transactionManager.remove(id);
    }
}
