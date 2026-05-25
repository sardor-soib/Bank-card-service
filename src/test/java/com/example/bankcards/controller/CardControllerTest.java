package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.CreateCardDTO;
import com.example.bankcards.service.CardManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardControllerTest {

    @Mock
    CardManager cardManager;

    @InjectMocks
    CardController cardController;

    @Test
    void searchCards_delegatesToManager() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CardDTO> page = new PageImpl<>(List.of(CardDTO.builder().id(1L).build()));
        when(cardManager.search("1111", pageable)).thenReturn(page);

        assertThat(cardController.searchCards("1111", pageable)).isSameAs(page);
    }

    @Test
    void findAllCards_delegatesToManager() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CardDTO> page = new PageImpl<>(List.of());
        when(cardManager.findAll(pageable)).thenReturn(page);

        assertThat(cardController.findAllCards(pageable)).isSameAs(page);
    }

    @Test
    void getCard_delegatesToManager() {
        CardDTO dto = CardDTO.builder().id(5L).build();
        when(cardManager.findById(5L)).thenReturn(dto);

        assertThat(cardController.getCard(5L)).isSameAs(dto);
    }

    @Test
    void blockCard_invokesDeactivate() {
        cardController.blockCard(5L);

        verify(cardManager).deactivateCard(5L);
    }

    @Test
    void activateCard_invokesActivate() {
        cardController.activateCard(5L);

        verify(cardManager).activateCard(5L);
    }

    @Test
    void createCard_delegatesToManager() {
        CreateCardDTO input = new CreateCardDTO("4111111111111111", "John", "2030-01-01",
                "VISA", "DEBIT", BigDecimal.ZERO, "ACTIVE", 1L);
        CardDTO returned = CardDTO.builder().id(1L).build();
        when(cardManager.create(input)).thenReturn(returned);

        assertThat(cardController.createCard(input)).isSameAs(returned);
    }

    @Test
    void updateCard_delegatesToManager() {
        CardDTO input = CardDTO.builder().id(5L).build();
        CardDTO returned = CardDTO.builder().id(5L).build();
        when(cardManager.update(5L, input)).thenReturn(returned);

        assertThat(cardController.updateCard(5L, input)).isSameAs(returned);
    }

    @Test
    void deleteCard_invokesRemove() {
        cardController.deleteCard(5L);

        verify(cardManager).remove(5L);
    }
}
