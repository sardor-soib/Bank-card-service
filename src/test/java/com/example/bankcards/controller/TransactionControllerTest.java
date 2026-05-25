package com.example.bankcards.controller;

import com.example.bankcards.dto.TransactionDTO;
import com.example.bankcards.service.impl.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TransactionControllerTest {

    private TransactionService transactionService;
    private TransactionController controller;

    @BeforeEach
    void setUp() {
        transactionService = mock(TransactionService.class);
        controller = new TransactionController(transactionService);
    }

    @Test
    void findTransactionById_delegates() {
        TransactionDTO dto = TransactionDTO.builder().id(1L).build();
        when(transactionService.findById(1L)).thenReturn(dto);

        assertThat(controller.findTransactionById(1L)).isSameAs(dto);
    }

    @Test
    void findTransactionsForUser_delegatesToFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<TransactionDTO> page = new PageImpl<>(List.of());
        when(transactionService.findAll(pageable)).thenReturn(page);

        assertThat(controller.findTransactionsForUser(1L, pageable)).isSameAs(page);
    }

    @Test
    void findTransactionsForCard_delegates() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<TransactionDTO> page = new PageImpl<>(List.of());
        when(transactionService.findByCardId(7L, pageable)).thenReturn(page);

        assertThat(controller.findTransactionsForCard(7L, pageable)).isSameAs(page);
    }

    @Test
    void createTransaction_delegates() {
        TransactionDTO dto = TransactionDTO.builder().build();
        TransactionDTO returned = TransactionDTO.builder().id(1L).build();
        when(transactionService.create(dto)).thenReturn(returned);

        assertThat(controller.createTransaction(dto)).isSameAs(returned);
    }

    @Test
    void updateTransaction_delegates() {
        TransactionDTO dto = TransactionDTO.builder().build();
        TransactionDTO returned = TransactionDTO.builder().id(1L).build();
        when(transactionService.update(1L, dto)).thenReturn(returned);

        assertThat(controller.updateTransaction(1L, dto)).isSameAs(returned);
    }

    @Test
    void deleteTransaction_delegates() {
        controller.deleteTransaction(1L);

        verify(transactionService).remove(1L);
    }
}
