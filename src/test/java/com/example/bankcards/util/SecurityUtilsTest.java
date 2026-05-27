package com.example.bankcards.util;

import com.example.bankcards.config.CustomUserDetails;
import com.example.bankcards.entity.User;
import com.example.bankcards.security.SecurityUtils;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SecurityUtilsTest {

    @SuppressWarnings("unchecked")
    private Authentication auth(Object principal, String role) {
        Authentication a = mock(Authentication.class);
        when(a.getPrincipal()).thenReturn(principal);
        when(a.getAuthorities()).thenReturn((List) List.of(new SimpleGrantedAuthority(role)));
        return a;
    }

    private CustomUserDetails userDetails(Long id, Role role) {
        User user = mock(User.class);
        when(user.getId()).thenReturn(id);
        when(user.getRole()).thenReturn(role);
        when(user.getStatus()).thenReturn(UserStatus.ACTIVE);
        return new CustomUserDetails(user);
    }

    @Test
    void isAdmin_adminRole_returnsTrue() {
        assertThat(SecurityUtils.isAdmin(auth(mock(CustomUserDetails.class), "ROLE_ADMIN"))).isTrue();
    }

    @Test
    void isAdmin_userRole_returnsFalse() {
        assertThat(SecurityUtils.isAdmin(auth(mock(CustomUserDetails.class), "ROLE_USER"))).isFalse();
    }

    @Test
    void isUser_userRole_returnsTrue() {
        assertThat(SecurityUtils.isUser(auth(mock(CustomUserDetails.class), "ROLE_USER"))).isTrue();
    }

    @Test
    void isUser_adminRole_returnsFalse() {
        assertThat(SecurityUtils.isUser(auth(mock(CustomUserDetails.class), "ROLE_ADMIN"))).isFalse();
    }

    @Test
    void getCurrentUserId_validPrincipal_returnsId() {
        CustomUserDetails details = userDetails(42L, Role.USER);
        Authentication a = auth(details, "ROLE_USER");

        assertThat(SecurityUtils.getCurrentUserId(a)).isEqualTo(42L);
    }

    @Test
    void getCurrentUserId_nullAuth_throws() {
        assertThatThrownBy(() -> SecurityUtils.getCurrentUserId(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getCurrentUserId_wrongPrincipalType_throws() {
        Authentication a = auth("not-a-custom-user-details", "ROLE_USER");

        assertThatThrownBy(() -> SecurityUtils.getCurrentUserId(a))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CustomUserDetails");
    }

    @Test
    void isAdmin_nullAuth_throws() {
        assertThatThrownBy(() -> SecurityUtils.isAdmin(null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
