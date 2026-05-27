package com.example.bankcards.entity;

import com.example.bankcards.util.Currency;
import com.example.bankcards.util.TransactionStatus;
import com.example.bankcards.util.TransactionType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TransactionStatus status;

    @Column(name = "transaction_time", nullable = false)
    private OffsetDateTime transactionTime;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    public static Transaction createTransaction(
            Card card,
            User user,
            BigDecimal amount,
            Currency currency,
            TransactionType transactionType,
            TransactionStatus status
    ) {
        OffsetDateTime now = OffsetDateTime.now();

        Transaction transaction = new Transaction();
        transaction.card = card;
        transaction.user = user;
        transaction.amount = amount;
        transaction.currency = currency;
        transaction.transactionType = transactionType;
        transaction.status = status;
        transaction.transactionTime = now;
        transaction.createdAt = now;

        return transaction;
    }

    public Long getId() {
        return id;
    }

    public Card getCard() {
        return card;
    }

    public User getUser() {
        return user;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public OffsetDateTime getTransactionTime() {
        return transactionTime;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}