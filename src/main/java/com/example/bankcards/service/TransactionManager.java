package com.example.bankcards.service;

import com.example.bankcards.dto.TransactionDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface TransactionManager {

    boolean isExists(@NotNull Long id);

    void transferBetweenUserCards(@NotNull Long userId, @NotNull Long sourceCardId, @NotNull Long targetCardId, @NotNull @Valid BigDecimal amount);

    TransactionDTO create(@NotNull TransactionDTO transactionDTO);

    TransactionDTO findById(@NotNull Long id);

    Page<TransactionDTO> findByUserId(@NotNull Long userId, Pageable pageable);

    Page<TransactionDTO> findByCardId(@NotNull Long cardId, Pageable pageable);

    Page<TransactionDTO> findAll(Pageable pageable);

    TransactionDTO update(@NotNull Long id, @NotNull TransactionDTO transactionDTO);

    void remove(@NotNull Long id);
}
