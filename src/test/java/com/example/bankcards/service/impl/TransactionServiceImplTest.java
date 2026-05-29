package com.example.bankcards.service.impl;

import com.example.bankcards.dto.TransactionDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transaction;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.repository.TransactionRepository;
import com.example.bankcards.util.mapper.TransactionMapper;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    TransactionRepository transactionRepository;
    @Mock
    TransactionMapper transactionMapper;

    @InjectMocks
    TransactionServiceImpl transactionServiceImpl;

    @Test
    void isExists_delegatesToRepository() {
        when(transactionRepository.existsById(1L)).thenReturn(true);

        assertThat(transactionServiceImpl.isExists(1L)).isTrue();
    }

    @Test
    void formTransactions_savesDebitAndCreditTransactions() {
        User user = mock(User.class);
        Card source = mock(Card.class);
        Card target = mock(Card.class);
        when(source.getUser()).thenReturn(user);
        when(target.getUser()).thenReturn(user);

        transactionServiceImpl.formTransactions(source, target, new BigDecimal("25"));

        verify(transactionRepository, times(2)).save(any(Transaction.class));
    }

    @Test
    void findById_existing_returnsDto() {
        Transaction transaction = mock(Transaction.class);
        TransactionDTO dto = TransactionDTO.builder().id(1L).build();

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
        when(transactionMapper.toTransactionDTO(transaction)).thenReturn(dto);

        assertThat(transactionServiceImpl.findById(1L)).isSameAs(dto);
    }

    @Test
    void findById_missing_throws() {
        when(transactionRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionServiceImpl.findById(404L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findByUserId_delegatesToRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Transaction> page = new PageImpl<>(List.of());
        Page<TransactionDTO> dtoPage = new PageImpl<>(List.of());

        when(transactionRepository.findByUserId(1L, pageable)).thenReturn(page);
        when(transactionMapper.toTransactionDTOPage(page)).thenReturn(dtoPage);

        assertThat(transactionServiceImpl.findByUserId(1L, pageable)).isSameAs(dtoPage);
    }

    @Test
    void findByCardId_delegatesToRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Transaction> page = new PageImpl<>(List.of());
        Page<TransactionDTO> dtoPage = new PageImpl<>(List.of());

        when(transactionRepository.findByCardId(7L, pageable)).thenReturn(page);
        when(transactionMapper.toTransactionDTOPage(page)).thenReturn(dtoPage);

        assertThat(transactionServiceImpl.findByCardId(7L, pageable)).isSameAs(dtoPage);
    }

    @Test
    void findAll_delegatesToRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Transaction> page = new PageImpl<>(List.of());
        Page<TransactionDTO> dtoPage = new PageImpl<>(List.of());

        when(transactionRepository.findAll(pageable)).thenReturn(page);
        when(transactionMapper.toTransactionDTOPage(page)).thenReturn(dtoPage);

        assertThat(transactionServiceImpl.findAll(pageable)).isSameAs(dtoPage);
    }
}