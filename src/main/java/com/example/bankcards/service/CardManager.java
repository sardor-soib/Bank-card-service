package com.example.bankcards.service;

import com.example.bankcards.dto.CardDTO;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CardManager {

    boolean isExists(@NotNull Long id);

    CardDTO create(@NotNull CardDTO cardDto);

    CardDTO findById(@NotNull Long id);

    Page<CardDTO> findByUserId(@NotNull Long userId, Pageable pageable);

    Page<CardDTO> search(@NotNull String query, Pageable pageable);

    Page<CardDTO> findAll(Pageable pageable);

    CardDTO update(@NotNull Long id, @NotNull CardDTO cardDto);

    void remove(@NotNull Long id);

}
