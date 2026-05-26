package com.example.bankcards.entity;

import com.example.bankcards.util.*;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionTest {

    private static Object read(Transaction t, String fieldName) throws Exception {
        Field f = Transaction.class.getDeclaredField(fieldName);
        f.setAccessible(true);
        return f.get(t);
    }

    @Test
    void createTransaction_populatesAllFieldsAndTimestamps() throws Exception {
        User user = new User("Jane", "jane@example.com", "+1", "hash",
                Role.USER, UserStatus.ACTIVE);
        Card card = new Card(LocalDate.of(2030, 1, 1), CardBrand.VISA, CardType.DEBIT,
                CardStatus.ACTIVE, new BigDecimal("100"), user);

        OffsetDateTime before = OffsetDateTime.now();
        Transaction t = Transaction.createTransaction(card, user, new BigDecimal("12.50"),
                Currency.USD, TransactionType.DEBIT, TransactionStatus.POSTED);
        OffsetDateTime after = OffsetDateTime.now();

        assertThat(read(t, "card")).isSameAs(card);
        assertThat(read(t, "user")).isSameAs(user);
        assertThat((BigDecimal) read(t, "amount")).isEqualByComparingTo("12.50");
        assertThat(read(t, "currency")).isEqualTo(Currency.USD);
        assertThat(read(t, "transactionType")).isEqualTo(TransactionType.DEBIT);
        assertThat(read(t, "status")).isEqualTo(TransactionStatus.POSTED);

        OffsetDateTime transactionTime = (OffsetDateTime) read(t, "transactionTime");
        OffsetDateTime createdAt = (OffsetDateTime) read(t, "createdAt");

        assertThat(transactionTime).isEqualTo(createdAt).isBetween(before, after);
    }
}
