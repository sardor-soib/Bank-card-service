package com.example.bankcards.entity;

import com.example.bankcards.util.CardBrand;
import com.example.bankcards.util.CardStatus;
import com.example.bankcards.util.CardType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "cards")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id", nullable = false)
    private Long id;

    @Column(name = "bin_number", nullable = false)
    private String binNumber;

    @Column(name = "last_four", nullable = false)
    private String lastFour;

    @Column(name = "masked_pan", nullable = false)
    private String maskedPan;

    @Column(name = "pan_hash", nullable = false)
    private String panHash;

    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    @Column(name = "card_brand", nullable = false)
    @Enumerated(EnumType.STRING)
    private CardBrand cardBrand;

    @Column(name = "card_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private CardType cardType;

    @Column(name = "card_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private CardStatus cardStatus;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User user;

    @OneToMany(mappedBy = "card")
    private Set<Transaction> transactions;

    public Card() {
    }

    public Card(LocalDate expirationDate, CardBrand cardBrand, CardType cardType,
                CardStatus cardStatus, BigDecimal balance, User user) {
        this.expirationDate = expirationDate;
        this.cardBrand = cardBrand;
        this.cardType = cardType;
        this.cardStatus = cardStatus;
        this.balance = balance;
        this.user = user;
    }

    public void applyPanData(String binNumber, String lastFour, String maskedPan, String panHash) {
        this.binNumber = binNumber;
        this.lastFour = lastFour;
        this.maskedPan = maskedPan;
        this.panHash = panHash;
    }

    public void debit(BigDecimal amount) {
        this.balance = this.balance.subtract(amount);
    }

    public void credit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    public void blockCard() {
        this.cardStatus = CardStatus.BLOCKED;
    }

    public void unblockCard() {
        this.cardStatus = CardStatus.ACTIVE;
    }

    public void requestBlock() {
        this.cardStatus = CardStatus.BLOCK_REQUESTED;
    }

    public Long getId() {
        return id;
    }

    public String getBinNumber() {
        return binNumber;
    }

    public String getLastFour() {
        return lastFour;
    }

    public String getMaskedPan() {
        return maskedPan;
    }

    public String getPanHash() {
        return panHash;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public CardBrand getCardBrand() {
        return cardBrand;
    }

    public CardType getCardType() {
        return cardType;
    }

    public CardStatus getCardStatus() {
        return cardStatus;
    }

    public Set<Transaction> getTransactions() {
        return transactions;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public User getUser() {
        return user;
    }
}