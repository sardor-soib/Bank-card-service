package com.example.bankcards.service.validation;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.util.CardStatus;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class TransactionValidatorTest {

    private static final Long USER_ID = 7L;

    private Card userCard(Long cardId, CardStatus status, BigDecimal balance) {
        User user = Mockito.mock(User.class);
        Mockito.when(user.getId()).thenReturn(USER_ID);
        Card card = Mockito.mock(Card.class);
        Mockito.when(card.getId()).thenReturn(cardId);
        Mockito.when(card.getUser()).thenReturn(user);
        Mockito.when(card.getCardStatus()).thenReturn(status);
        Mockito.when(card.getBalance()).thenReturn(balance);
        return card;
    }

    @Test
    void validateTransfer_validInputs_doesNotThrow() {
        Card source = userCard(1L, CardStatus.ACTIVE, new BigDecimal("100"));
        Card target = userCard(2L, CardStatus.ACTIVE, new BigDecimal("0"));

        assertThatCode(() ->
                TransactionValidator.validateTransferBetweenUserCards(USER_ID, source, target, new BigDecimal("25"))
        ).doesNotThrowAnyException();
    }

    @Test
    void validateTransfer_sameCard_throws() {
        Card source = userCard(1L, CardStatus.ACTIVE, new BigDecimal("100"));

        Throwable thrown = catchThrowable(() -> TransactionValidator.validateTransferBetweenUserCards(USER_ID, source, source, new BigDecimal("10")));

        assertThat(thrown).as("Expected IllegalArgumentException for same card transfer")
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("must be different");
    }

    @Test
    void validateTransfer_inactiveSource_throws() {
        Card source = userCard(1L, CardStatus.BLOCKED, new BigDecimal("100"));
        Card target = userCard(2L, CardStatus.ACTIVE, new BigDecimal("0"));

        Throwable thrown = catchThrowable(() -> TransactionValidator.validateTransferBetweenUserCards(USER_ID, source, target, new BigDecimal("10")));

        assertThat(thrown).as("Expected IllegalArgumentException for inactive source card")
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("not active");
    }

    @Test
    void validateTransfer_inactiveTarget_throws() {
        Card source = userCard(1L, CardStatus.ACTIVE, new BigDecimal("100"));
        Card target = userCard(2L, CardStatus.FROZEN, new BigDecimal("0"));

        Throwable thrown = catchThrowable(() -> TransactionValidator.validateTransferBetweenUserCards(USER_ID, source, target, new BigDecimal("10")));

        assertThat(thrown).as("Expected IllegalArgumentException for inactive target card")
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("not active");
    }

    @Test
    void validateTransfer_nonOwnerSource_throws() {
        Card source = userCard(1L, CardStatus.ACTIVE, new BigDecimal("100"));
        User otherUser = Mockito.mock(User.class);
        Mockito.when(otherUser.getId()).thenReturn(99L);
        Card target = Mockito.mock(Card.class);
        Mockito.when(target.getId()).thenReturn(2L);
        Mockito.when(target.getUser()).thenReturn(otherUser);
        Mockito.when(target.getCardStatus()).thenReturn(CardStatus.ACTIVE);

        Throwable thrown = catchThrowable(() -> TransactionValidator.validateTransferBetweenUserCards(USER_ID, source, target, new BigDecimal("10")));

        assertThat(thrown).as("Expected IllegalArgumentException for non-owner source card")
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("must belong to the user");
    }

    @Test
    void validateTransfer_zeroAmount_throws() {
        Card source = userCard(1L, CardStatus.ACTIVE, new BigDecimal("100"));
        Card target = userCard(2L, CardStatus.ACTIVE, new BigDecimal("0"));

        Throwable thrown = catchThrowable(() ->
                TransactionValidator.validateTransferBetweenUserCards(USER_ID, source, target, BigDecimal.ZERO));

        assertThat(thrown).as("Expected IllegalArgumentException for zero amount").isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("positive");
    }

    @Test
    void validateTransfer_negativeAmount_throws() {
        Card source = userCard(1L, CardStatus.ACTIVE, new BigDecimal("100"));
        Card target = userCard(2L, CardStatus.ACTIVE, new BigDecimal("0"));

        Throwable thrown = catchThrowable(() ->
                TransactionValidator.validateTransferBetweenUserCards(USER_ID, source, target, new BigDecimal("-1")));

        assertThat(thrown).as("Expected IllegalArgumentException for negative amount").isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("positive");
    }

    @Test
    void validateTransfer_nullAmount_throws() {
        Card source = userCard(1L, CardStatus.ACTIVE, new BigDecimal("100"));
        Card target = userCard(2L, CardStatus.ACTIVE, new BigDecimal("0"));

        Throwable thrown = catchThrowable(() ->
                TransactionValidator.validateTransferBetweenUserCards(USER_ID, source, target, null));

        assertThat(thrown).as("Expected IllegalArgumentException for null amount").isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("positive");
    }

    @Test
    void validateTransfer_insufficientFunds_throws() {
        Card source = userCard(1L, CardStatus.ACTIVE, new BigDecimal("5"));
        Card target = userCard(2L, CardStatus.ACTIVE, new BigDecimal("0"));

        Throwable thrown = catchThrowable(() ->
                TransactionValidator.validateTransferBetweenUserCards(USER_ID, source, target, new BigDecimal("10")));

        assertThat(thrown).as("Expected IllegalArgumentException for insufficient funds").isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Insufficient funds");
    }
}
