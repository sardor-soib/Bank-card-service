package com.example.bankcards.service.validation;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserValidatorTest {

    @Test
    void validateOwner_matchingUserId_doesNotThrow() {
        User user = Mockito.mock(User.class);
        Mockito.when(user.getId()).thenReturn(42L);
        Card card = Mockito.mock(Card.class);
        Mockito.when(card.getUser()).thenReturn(user);

        assertThatCode(() -> UserValidator.validateOwner(42L, card)).doesNotThrowAnyException();
    }

    @Test
    void validateOwner_differentUserId_throws() {
        User user = Mockito.mock(User.class);
        Mockito.when(user.getId()).thenReturn(42L);
        Card card = Mockito.mock(Card.class);
        Mockito.when(card.getUser()).thenReturn(user);

        assertThatThrownBy(() -> UserValidator.validateOwner(99L, card))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not the owner");
    }
}
