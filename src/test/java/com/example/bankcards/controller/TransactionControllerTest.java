package com.example.bankcards.controller;

import com.example.bankcards.config.CustomUserDetails;
import com.example.bankcards.dto.TransactionDTO;
import com.example.bankcards.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TransactionControllerTest {

    private TransactionServiceImpl transactionServiceImpl;
    private TransactionController controller;

    @BeforeEach
    void setUp() {
        transactionServiceImpl = mock(TransactionServiceImpl.class);
        controller = new TransactionController(transactionServiceImpl);
    }

    @Test
    void findTransactionById_delegates() {
        TransactionDTO dto = TransactionDTO.builder().id(1L).build();
        when(transactionServiceImpl.findById(1L)).thenReturn(dto);

        assertThat(controller.findTransactionById(1L)).isSameAs(dto);
    }

    @Test
    void findTransactionsForUser_delegatesToFindByUserId() {
        Authentication authentication = mock(Authentication.class);
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authentication.getAuthorities()).thenReturn(Collections.emptyList());
        when(userDetails.getId()).thenReturn(1L);

        Pageable pageable = PageRequest.of(0, 10);
        Page<TransactionDTO> page = new PageImpl<>(List.of());
        when(transactionServiceImpl.findByUserId(1L, pageable)).thenReturn(page);

        assertThat(controller.findTransactionsForUser(authentication, pageable)).isSameAs(page);
    }

    @Test
    void findTransactionsForCard_delegates() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<TransactionDTO> page = new PageImpl<>(List.of());
        when(transactionServiceImpl.findByCardId(7L, pageable)).thenReturn(page);

        assertThat(controller.findTransactionsForCard(7L, pageable)).isSameAs(page);
    }
}
