package com.example.bankcards.entity;

import com.example.bankcards.util.CardBrand;
import com.example.bankcards.util.CardStatus;
import com.example.bankcards.util.CardType;
import jakarta.persistence.*;

import java.math.BigDecimal;
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

    @Column(name = "expiration_month", nullable = false)
    private Integer expirationMonth;

    @Column(name = "expiration_year", nullable = false)
    private Integer expirationYear;

    @Column(name = "card_brand", nullable = false)
    @Enumerated(EnumType.STRING)
    private CardBrand cardBrand;

    @Column(name = "cvv_hash", nullable = false)
    private String cvvHash;

    @Column(name = "card_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private CardType cardType;

    @Column(name = "card_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private CardStatus cardStatus;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    @JoinColumn(name = "owner_id")
    private User user;

    @OneToMany(mappedBy = "card")
    private Set<Transaction> transactions;

    public Card() {
    }

    public Card(String binNumber, String lastFour, String maskedPan, String panHash, Integer expirationMonth,
                Integer expirationYear, CardBrand cardBrand, String cvvHash, CardType cardType, CardStatus cardStatus,
                BigDecimal balance, User user) {
        this.binNumber = binNumber;
        this.lastFour = lastFour;
        this.maskedPan = maskedPan;
        this.panHash = panHash;
        this.expirationMonth = expirationMonth;
        this.expirationYear = expirationYear;
        this.cardBrand = cardBrand;
        this.cvvHash = cvvHash;
        this.cardType = cardType;
        this.cardStatus = cardStatus;
        this.balance = balance;
        this.user = user;
    }

    public void debit(BigDecimal amount) {
        this.balance = this.balance.subtract(amount);
    }

    public void credit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
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

    public Integer getExpirationMonth() {
        return expirationMonth;
    }

    public Integer getExpirationYear() {
        return expirationYear;
    }

    public CardBrand getCardBrand() {
        return cardBrand;
    }

    public String getCvvHash() {
        return cvvHash;
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