package com.example.bankcards.controller;

import com.example.bankcards.config.CustomUserDetails;
import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.service.CardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserCardControllerTest {

    @Mock
    CardService cardService;

    @InjectMocks
    UserCardController controller;

    private Authentication authentication;
    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() {
        authentication = mock(Authentication.class);
        userDetails = mock(CustomUserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authentication.getAuthorities()).thenReturn(Collections.emptyList());
        when(userDetails.getId()).thenReturn(1L);
    }

    @Test
    void getCardsForUser_delegates() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CardDTO> page = new PageImpl<>(List.of(CardDTO.builder().id(1L).build()));
        when(cardService.findByUserId(1L, pageable)).thenReturn(page);

        assertThat(controller.getCardsForUser(authentication, pageable)).isSameAs(page);
    }

    @Test
    void searchCardsForUser_delegates() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CardDTO> page = new PageImpl<>(List.of());
        when(cardService.search(1L, "1111", pageable)).thenReturn(page);

        assertThat(controller.searchCardsForUser(authentication, "1111", pageable)).isSameAs(page);
    }

    @Test
    void transferBalance_delegates() {
        BigDecimal amount = new BigDecimal("50.00");
        controller.transferBalance(authentication, 1L, 2L, new TransferRequest(amount));

        verify(cardService).transferBalance(1L, 1L, 2L, amount);
    }

    @Test
    void requestCardBlock_delegates() {
        controller.requestCardBlock(authentication, 5L);

        verify(cardService).requestCardBlock(1L, 5L);
    }

    @Test
    void getBalance_delegates() {
        when(cardService.getBalance(1L, 5L)).thenReturn(new BigDecimal("100.00"));

        assertThat(controller.getBalance(authentication, 5L)).isEqualByComparingTo("100.00");
    }
}