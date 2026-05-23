package com.example.bankcards.entity;

import com.example.bankcards.util.CardBrand;
import com.example.bankcards.util.CardStatus;
import com.example.bankcards.util.CardType;
import jakarta.persistence.*;

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

    @Column(name = "cvv", nullable = false)
    private Integer cvv;

    @Column(name = "card_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private CardType cardType;

    @Column(name = "card_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private CardStatus cardStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User user;

    @OneToMany(mappedBy = "card")
    private Set<Transaction> transactions;

    public Card() {
    }

    public Card(String binNumber, String lastFour, String maskedPan, String panHash, Integer expirationMonth,
                Integer expirationYear, CardBrand cardBrand, Integer cvv, CardType cardType, CardStatus cardStatus,
                User user) {
        this.binNumber = binNumber;
        this.lastFour = lastFour;
        this.maskedPan = maskedPan;
        this.panHash = panHash;
        this.expirationMonth = expirationMonth;
        this.expirationYear = expirationYear;
        this.cardBrand = cardBrand;
        this.cvv = cvv;
        this.cardType = cardType;
        this.cardStatus = cardStatus;
        this.user = user;
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

    public Integer getCvv() {
        return cvv;
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

    public User getUser() {
        return user;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setBinNumber(String binNumber) {
        this.binNumber = binNumber;
    }

    public void setLastFour(String lastFour) {
        this.lastFour = lastFour;
    }

    public void setMaskedPan(String maskedPan) {
        this.maskedPan = maskedPan;
    }

    public void setPanHash(String panHash) {
        this.panHash = panHash;
    }

    public void setExpirationMonth(Integer expirationMonth) {
        this.expirationMonth = expirationMonth;
    }

    public void setExpirationYear(Integer expirationYear) {
        this.expirationYear = expirationYear;
    }

    public void setCardBrand(CardBrand cardBrand) {
        this.cardBrand = cardBrand;
    }

    public void setCvv(Integer cvv) {
        this.cvv = cvv;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

    public void setCardStatus(CardStatus cardStatus) {
        this.cardStatus = cardStatus;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setTransactions(Set<Transaction> transactions) {
        this.transactions = transactions;
    }
}