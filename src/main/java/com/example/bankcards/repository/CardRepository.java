package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.util.CardStatus;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    Page<Card> findByUserId(@NotNull Long userId, Pageable pageable);

    Page<Card> findByLastFour(@NotNull String lastFour, Pageable pageable);

    Page<Card> findByUserIdAndLastFour(@NotNull Long userId, @NotNull String lastFour, Pageable pageable);

    List<Card> findByCardStatusAndExpirationDateBefore(CardStatus cardStatus, LocalDate date);
}