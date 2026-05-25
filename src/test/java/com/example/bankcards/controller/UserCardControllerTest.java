package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDTO;
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
class UserCardControllerTest {

    @Mock
    CardManager cardManager;

    @InjectMocks
    UserCardController controller;

    @Test
    void getCardsForUser_delegates() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CardDTO> page = new PageImpl<>(List.of(CardDTO.builder().id(1L).build()));
        when(cardManager.findByUserId(1L, pageable)).thenReturn(page);

        assertThat(controller.getCardsForUser(1L, pageable)).isSameAs(page);
    }

    @Test
    void searchCardsForUser_delegates() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CardDTO> page = new PageImpl<>(List.of());
        when(cardManager.search(1L, "1111", pageable)).thenReturn(page);

        assertThat(controller.searchCardsForUser(1L, "1111", pageable)).isSameAs(page);
    }

    @Test
    void requestCardBlock_delegates() {
        controller.requestCardBlock(1L, 5L);

        verify(cardManager).requestCardBlock(1L, 5L);
    }

    @Test
    void getBalance_delegates() {
        when(cardManager.getBalance(1L, 5L)).thenReturn(new BigDecimal("100.00"));

        assertThat(controller.getBalance(1L, 5L)).isEqualByComparingTo("100.00");
    }
}
