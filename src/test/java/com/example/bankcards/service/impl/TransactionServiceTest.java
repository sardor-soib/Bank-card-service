package com.example.bankcards.service.impl;

import com.example.bankcards.dto.TransactionDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transaction;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.TransactionRepository;
import com.example.bankcards.util.TransactionMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    TransactionRepository transactionRepository;
    @Mock
    TransactionMapper transactionMapper;

    @InjectMocks
    TransactionService transactionService;

    @Test
    void isExists_delegatesToRepository() {
        when(transactionRepository.existsById(1L)).thenReturn(true);

        assertThat(transactionService.isExists(1L)).isTrue();
    }

    @Test
    void formTransactions_savesDebitAndCreditTransactions() {
        User user = org.mockito.Mockito.mock(User.class);
        Card source = org.mockito.Mockito.mock(Card.class);
        Card target = org.mockito.Mockito.mock(Card.class);
        when(source.getUser()).thenReturn(user);
        when(target.getUser()).thenReturn(user);

        transactionService.formTransactions(source, target, new BigDecimal("25"));

        verify(transactionRepository, times(2)).save(any(Transaction.class));
    }

    @Test
    void create_savesAndMapsBack() {
        TransactionDTO input = TransactionDTO.builder().amount(new BigDecimal("10")).build();
        Transaction mapped = org.mockito.Mockito.mock(Transaction.class);
        Transaction saved = org.mockito.Mockito.mock(Transaction.class);
        TransactionDTO returned = TransactionDTO.builder().id(99L).build();

        when(transactionMapper.toTransaction(input)).thenReturn(mapped);
        when(transactionRepository.save(mapped)).thenReturn(saved);
        when(transactionMapper.toTransactionDTO(saved)).thenReturn(returned);

        assertThat(transactionService.create(input)).isSameAs(returned);
    }

    @Test
    void findById_existing_returnsDto() {
        Transaction transaction = org.mockito.Mockito.mock(Transaction.class);
        TransactionDTO dto = TransactionDTO.builder().id(1L).build();

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
        when(transactionMapper.toTransactionDTO(transaction)).thenReturn(dto);

        assertThat(transactionService.findById(1L)).isSameAs(dto);
    }

    @Test
    void findById_missing_throws() {
        when(transactionRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.findById(404L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findByUserId_delegatesToRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Transaction> page = new PageImpl<>(List.of());
        Page<TransactionDTO> dtoPage = new PageImpl<>(List.of());

        when(transactionRepository.findByUserId(1L, pageable)).thenReturn(page);
        when(transactionMapper.toTransactionDTOPage(page)).thenReturn(dtoPage);

        assertThat(transactionService.findByUserId(1L, pageable)).isSameAs(dtoPage);
    }

    @Test
    void findByCardId_delegatesToRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Transaction> page = new PageImpl<>(List.of());
        Page<TransactionDTO> dtoPage = new PageImpl<>(List.of());

        when(transactionRepository.findByCardId(7L, pageable)).thenReturn(page);
        when(transactionMapper.toTransactionDTOPage(page)).thenReturn(dtoPage);

        assertThat(transactionService.findByCardId(7L, pageable)).isSameAs(dtoPage);
    }

    @Test
    void findAll_delegatesToRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Transaction> page = new PageImpl<>(List.of());
        Page<TransactionDTO> dtoPage = new PageImpl<>(List.of());

        when(transactionRepository.findAll(pageable)).thenReturn(page);
        when(transactionMapper.toTransactionDTOPage(page)).thenReturn(dtoPage);

        assertThat(transactionService.findAll(pageable)).isSameAs(dtoPage);
    }

    @Test
    void update_existing_saves() {
        TransactionDTO dto = TransactionDTO.builder().id(1L).build();
        Transaction mapped = org.mockito.Mockito.mock(Transaction.class);
        Transaction saved = org.mockito.Mockito.mock(Transaction.class);
        TransactionDTO returned = TransactionDTO.builder().id(1L).build();

        when(transactionRepository.existsById(1L)).thenReturn(true);
        when(transactionMapper.toTransaction(dto)).thenReturn(mapped);
        when(transactionRepository.save(mapped)).thenReturn(saved);
        when(transactionMapper.toTransactionDTO(saved)).thenReturn(returned);

        assertThat(transactionService.update(1L, dto)).isSameAs(returned);
    }

    @Test
    void update_missing_throws() {
        when(transactionRepository.existsById(404L)).thenReturn(false);

        Throwable thrown = catchThrowable(() -> transactionService.update(404L, TransactionDTO.builder().id(404L).build()));

        assertThat(thrown).as("Expected ResourceNotFoundException for missing transaction").isInstanceOf(ResourceNotFoundException.class);

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void remove_existing_deletes() {
        when(transactionRepository.existsById(1L)).thenReturn(true);

        transactionService.remove(1L);

        verify(transactionRepository).deleteById(1L);
    }

    @Test
    void remove_missing_throws() {
        when(transactionRepository.existsById(404L)).thenReturn(false);

        assertThatThrownBy(() -> transactionService.remove(404L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(transactionRepository, never()).deleteById(any());
    }
}
