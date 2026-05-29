package com.example.bankcards.service;

import com.example.bankcards.dto.TransactionDTO;
import com.example.bankcards.entity.Card;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface TransactionService {

    boolean isExists(@NotNull Long id);

    void formTransactions(@NotNull Card sourceCard, @NotNull Card targetCard, @NotNull BigDecimal amount);

    TransactionDTO findById(@NotNull Long id);

    Page<TransactionDTO> findByUserId(@NotNull Long userId, Pageable pageable);

    Page<TransactionDTO> findByCardId(@NotNull Long cardId, Pageable pageable);

    Page<TransactionDTO> findAll(Pageable pageable);
}