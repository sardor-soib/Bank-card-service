package com.example.bankcards.entity;

import com.example.bankcards.util.CardBrand;
import com.example.bankcards.util.CardStatus;
import com.example.bankcards.util.CardType;
import com.example.bankcards.util.Currency;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false)
    private Currency currency;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Card card = (Card) o;
        return getId().equals(card.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    public void applyPanData(String binNumber, String lastFour, String maskedPan, String panHash) {
        this.binNumber = binNumber;
        this.lastFour = lastFour;
        this.maskedPan = maskedPan;
        this.panHash = panHash;
    }

    public void debit(BigDecimal amount) {
        if (this.balance.compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient funds");
        }
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public CardType getCardType() {
        return cardType;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

    public CardBrand getCardBrand() {
        return cardBrand;
    }

    public void setCardBrand(CardBrand cardBrand) {
        this.cardBrand = cardBrand;
    }

    public CardStatus getCardStatus() {
        return cardStatus;
    }

    public void setCardStatus(CardStatus cardStatus) {
        this.cardStatus = cardStatus;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getPanHash() {
        return panHash;
    }

    public void setPanHash(String panHash) {
        this.panHash = panHash;
    }

    public String getMaskedPan() {
        return maskedPan;
    }

    public void setMaskedPan(String maskedPan) {
        this.maskedPan = maskedPan;
    }

    public String getLastFour() {
        return lastFour;
    }

    public void setLastFour(String lastFour) {
        this.lastFour = lastFour;
    }

    public String getBinNumber() {
        return binNumber;
    }

    public void setBinNumber(String binNumber) {
        this.binNumber = binNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}