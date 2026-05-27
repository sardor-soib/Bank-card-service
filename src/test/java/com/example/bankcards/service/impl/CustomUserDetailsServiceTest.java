package com.example.bankcards.service.impl;

import com.example.bankcards.config.CustomUserDetails;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.UserStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsername_existingEmail_returnsCustomUserDetails() {
        User user = mock(User.class);
        when(user.getEmail()).thenReturn("alice@example.com");
        when(user.getStatus()).thenReturn(UserStatus.ACTIVE);
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));

        UserDetails result = customUserDetailsService.loadUserByUsername("alice@example.com");

        assertThat(result).isInstanceOf(CustomUserDetails.class);
        assertThat(result.getUsername()).isEqualTo("alice@example.com");
        assertThat(result.isEnabled()).isTrue();
    }

    @Test
    void loadUserByUsername_inactiveUser_isNotEnabled() {
        User user = mock(User.class);
        when(user.getStatus()).thenReturn(UserStatus.INACTIVE);
        when(userRepository.findByEmail("inactive@example.com")).thenReturn(Optional.of(user));

        UserDetails result = customUserDetailsService.loadUserByUsername("inactive@example.com");

        assertThat(result.isEnabled()).isFalse();
    }

    @Test
    void loadUserByUsername_unknownEmail_throwsUsernameNotFoundException() {
        when(userRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername("ghost@example.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("ghost@example.com");
    }
}

