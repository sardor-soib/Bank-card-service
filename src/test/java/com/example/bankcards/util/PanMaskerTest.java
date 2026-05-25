package com.example.bankcards.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PanMaskerTest {

    @Test
    void mask_replacesAllButLastFour() {
        assertThat(PanMasker.mask("4111111111111111")).isEqualTo("**** **** **** 1111");
    }

    @Test
    void mask_stripsWhitespaceAndDashes() {
        assertThat(PanMasker.mask("4111 1111-1111 1234")).isEqualTo("**** **** **** 1234");
    }

    @Test
    void mask_supportsAmex15Digit() {
        assertThat(PanMasker.mask("378282246310005")).isEqualTo("**** **** **** 0005");
    }

    @Test
    void bin_returnsFirstSixDigits() {
        assertThat(PanMasker.bin("4111-1111-1111-1111")).isEqualTo("411111");
    }

    @Test
    void lastFour_returnsLastFourDigits() {
        assertThat(PanMasker.lastFour("4111 1111 1111 2345")).isEqualTo("2345");
    }

    @Test
    void mask_nullPan_throws() {
        assertThatThrownBy(() -> PanMasker.mask(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("PAN must not be null");
    }

    @Test
    void mask_tooShort_throws() {
        assertThatThrownBy(() -> PanMasker.mask("12345"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("12 to 19 digits");
    }

    @Test
    void mask_containsLetters_throws() {
        assertThatThrownBy(() -> PanMasker.mask("4111aaaa11111111"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("12 to 19 digits");
    }

    @Test
    void mask_tooLong_throws() {
        assertThatThrownBy(() -> PanMasker.mask("12345678901234567890"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("12 to 19 digits");
    }
}
