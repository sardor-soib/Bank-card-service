package com.example.bankcards.service;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.CreateCardDTO;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface CardService {

    boolean isExists(@NotNull Long id);

    void transferBalance(@NotNull Long userId, @NotNull Long sourceCardId, @NotNull Long targetCardId, @NotNull BigDecimal amount);

    void activateCard(@NotNull Long cardId);

    void deactivateCard(@NotNull Long cardId);

    void requestCardBlock(@NotNull Long userId, @NotNull Long cardId);

    BigDecimal getBalance(@NotNull Long userId, @NotNull Long cardId);

    CardDTO create(@NotNull CreateCardDTO createCardDTO);

    CardDTO findById(@NotNull Long id);

    Page<CardDTO> findByUserId(@NotNull Long userId, Pageable pageable);

    Page<CardDTO> search(@NotNull String query, Pageable pageable);

    Page<CardDTO> search(@NotNull Long userId, @NotNull String query, Pageable pageable);

    Page<CardDTO> findAll(Pageable pageable);

    CardDTO update(@NotNull Long id, @NotNull CardDTO cardDto);

    void remove(@NotNull Long id);
}
