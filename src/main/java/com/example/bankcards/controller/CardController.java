package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.CreateCardDTO;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@Tag(name = "Card Controller", description = "Card Management Controller")
@RequestMapping("/api/v1/cards")
@PreAuthorize("hasRole('ADMIN')")
public class CardController {

    private final CardService cardService;

    @Autowired
    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @Operation(summary = "Search cards", description = "Retrieve a list of cards by pan")
    @GetMapping("/search")
    public Page<CardDTO> searchCards(@RequestParam(name = "query") String query, Pageable pageable) {
        return cardService.search(query, pageable);
    }

    @Operation(summary = "Find all cards", description = "Retrieve all cards with pagination")
    @GetMapping
    public Page<CardDTO> findAllCards(Pageable pageable) {
        return cardService.findAll(pageable);
    }

    @Operation(summary = "Get card by id", description = "Retrieve a card by its unique identifier")
    @GetMapping("/{id}")
    public CardDTO getCard(@PathVariable Long id) {
        return cardService.findById(id);
    }

    @Operation(summary = "Block card", description = "Block a card by ID")
    @PatchMapping("/{id}/block")
    public void blockCard(@PathVariable Long id) {
        cardService.deactivateCard(id);
    }

    @Operation(summary = "Activate card", description = "Activate a card by ID")
    @PatchMapping("/{id}/activate")
    public void activateCard(@PathVariable Long id) {
        cardService.activateCard(id);
    }

    @Operation(summary = "Create a new card", description = "Add a new card to the database")
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public CardDTO createCard(@Valid @RequestBody CreateCardDTO createCardDTO) {
        return cardService.create(createCardDTO);
    }

    @Operation(summary = "Update an existing card", description = "Update details of an existing card by ID")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Card details to update", required = true)
    @PutMapping("/{id}")
    public CardDTO updateCard(@PathVariable Long id, @Valid @RequestBody CardDTO cardDto) {
        return cardService.update(id, cardDto);
    }

    @Operation(summary = "Delete a car", description = "Delete a card from the database by ID")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCard(@PathVariable Long id) {
        cardService.remove(id);
    }
}

