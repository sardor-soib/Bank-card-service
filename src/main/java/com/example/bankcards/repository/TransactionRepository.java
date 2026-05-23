package com.example.bankcards.repository;

import com.example.bankcards.entity.Transaction;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findByUserId(@NotNull Long userId, Pageable pageable);

    Page<Transaction> findByCardId(@NotNull Long cardId, Pageable pageable);
}
