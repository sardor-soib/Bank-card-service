package com.example.bankcards.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PanHashEncoderTest {

    private static final String SECRET = "test-secret-key";
    private final PanHashEncoder encoder = new PanHashEncoder(SECRET);

    @Test
    void encode_isDeterministic() {
        String hashA = encoder.encode("4111111111111111");
        String hashB = encoder.encode("4111111111111111");

        assertThat(hashA).isEqualTo(hashB);
    }

    @Test
    void encode_normalizesWhitespaceAndDashes() {
        String hashA = encoder.encode("4111111111111111");
        String hashB = encoder.encode("4111 1111-1111 1111");

        assertThat(hashA).isEqualTo(hashB);
    }

    @Test
    void encode_differentPan_differentHash() {
        String hashA = encoder.encode("4111111111111111");
        String hashB = encoder.encode("4111111111111234");

        assertThat(hashA).isNotEqualTo(hashB);
    }

    @Test
    void encode_differentSecret_differentHash() {
        PanHashEncoder other = new PanHashEncoder("another-secret");

        assertThat(encoder.encode("4111111111111111"))
                .isNotEqualTo(other.encode("4111111111111111"));
    }

    @Test
    void encode_returnsHexString() {
        String hash = encoder.encode("4111111111111111");

        assertThat(hash).matches("^[0-9a-f]{64}$");
    }

    @Test
    void matches_sameRawPanAndHash_true() {
        String hash = encoder.encode("4111111111111111");

        assertThat(encoder.matches("4111111111111111", hash)).isTrue();
    }

    @Test
    void matches_differentPan_false() {
        String hash = encoder.encode("4111111111111111");

        assertThat(encoder.matches("4111111111111234", hash)).isFalse();
    }

    @Test
    void encode_nullPan_throws() {
        assertThatThrownBy(() -> encoder.encode(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("PAN must not be null");
    }

    @Test
    void encode_invalidPan_throws() {
        assertThatThrownBy(() -> encoder.encode("abc"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("12 to 19 digits");
    }
}
