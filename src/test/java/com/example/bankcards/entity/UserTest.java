package com.example.bankcards.entity;

import com.example.bankcards.util.Role;
import com.example.bankcards.util.UserStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void activateUser_setsStatusActive() {
        User user = new User("Jane", "jane@example.com", "+1", "hash",
                Role.USER, UserStatus.INACTIVE);

        user.activateUser();

        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void deactivateUser_setsStatusInactive() {
        User user = new User("Jane", "jane@example.com", "+1", "hash",
                Role.USER, UserStatus.ACTIVE);

        user.deactivateUser();

        assertThat(user.getStatus()).isEqualTo(UserStatus.INACTIVE);
    }

    @Test
    void gettersExposeConstructorState() {
        User user = new User("Jane Doe", "jane@example.com", "+9999",
                "hashed-pw", Role.ADMIN, UserStatus.ACTIVE);

        assertThat(user.getFullName()).isEqualTo("Jane Doe");
        assertThat(user.getEmail()).isEqualTo("jane@example.com");
        assertThat(user.getPhoneNumber()).isEqualTo("+9999");
        assertThat(user.getPassword()).isEqualTo("hashed-pw");
        assertThat(user.getRole()).isEqualTo(Role.ADMIN);
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }
}
