package com.example.bankcards.entity;

import com.example.bankcards.util.CardBrand;
import com.example.bankcards.util.CardStatus;
import com.example.bankcards.util.CardType;
import com.example.bankcards.util.Role;
import com.example.bankcards.util.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class CardTest {

    private Card card;

    @BeforeEach
    void setUp() {
        User owner = new User("John Doe", "john@example.com", "+10000000000",
                "hash", Role.USER, UserStatus.ACTIVE);
        card = new Card(
                LocalDate.of(2030, 1, 1),
                CardBrand.VISA,
                CardType.DEBIT,
                CardStatus.ACTIVE,
                new BigDecimal("100.00"),
                owner
        );
    }

    @Test
    void applyPanData_setsAllPanFields() {
        card.applyPanData("411111", "1111", "**** **** **** 1111", "hashed");

        assertThat(card.getBinNumber()).isEqualTo("411111");
        assertThat(card.getLastFour()).isEqualTo("1111");
        assertThat(card.getMaskedPan()).isEqualTo("**** **** **** 1111");
        assertThat(card.getPanHash()).isEqualTo("hashed");
    }

    @Test
    void debit_subtractsFromBalance() {
        card.debit(new BigDecimal("25.50"));

        assertThat(card.getBalance()).isEqualByComparingTo("74.50");
    }

    @Test
    void credit_addsToBalance() {
        card.credit(new BigDecimal("50.00"));

        assertThat(card.getBalance()).isEqualByComparingTo("150.00");
    }

    @Test
    void blockCard_setsStatusToBlocked() {
        card.blockCard();

        assertThat(card.getCardStatus()).isEqualTo(CardStatus.BLOCKED);
    }

    @Test
    void unblockCard_setsStatusToActive() {
        card.blockCard();

        card.unblockCard();

        assertThat(card.getCardStatus()).isEqualTo(CardStatus.ACTIVE);
    }

    @Test
    void requestBlock_setsStatusToBlockRequested() {
        card.requestBlock();

        assertThat(card.getCardStatus()).isEqualTo(CardStatus.BLOCK_REQUESTED);
    }

    @Test
    void gettersExposeConstructorState() {
        assertThat(card.getCardBrand()).isEqualTo(CardBrand.VISA);
        assertThat(card.getCardType()).isEqualTo(CardType.DEBIT);
        assertThat(card.getExpirationDate()).isEqualTo(LocalDate.of(2030, 1, 1));
        assertThat(card.getUser().getEmail()).isEqualTo("john@example.com");
    }
}
