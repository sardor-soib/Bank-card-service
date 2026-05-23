package com.example.bankcards.entity;

import com.example.bankcards.util.Role;
import com.example.bankcards.util.UserStatus;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    Long id;

    @Column(name = "full_name", nullable = false)
    String fullName;

    @Column(name = "email", nullable = false, unique = true)
    String email;

    @Column(name = "phone_number", nullable = false)
    String phoneNumber;

    @Column(name = "password_hash", nullable = false)
    String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role")
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status")
    private UserStatus status;

    //Specific fields for Customer

    @OneToMany
    @Column(name = "cards")
    private Set<Card> cards;

    @OneToMany
    @Column(name = "transactions")
    private Set<Transaction> transactions;

    public User() {
    }

}
