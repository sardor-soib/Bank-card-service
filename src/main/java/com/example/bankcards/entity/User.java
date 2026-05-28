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

    @Column(name = "sub", unique = true)
    String sub;

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

    @OneToMany(mappedBy = "user")
    private Set<Card> cards;

    @OneToMany(mappedBy = "user")
    private Set<Transaction> transactions;

    public User(String fullName, String email, String phoneNumber, String password, Role role, UserStatus status) {
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.role = role;
        this.status = status;
    }

    public User() {

    }

    public void activateUser() {
        this.status = UserStatus.ACTIVE;
    }

    public void deactivateUser() {
        this.status = UserStatus.INACTIVE;
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public Set<Card> getCards() {
        return cards;
    }

    public Set<Transaction> getTransactions() {
        return transactions;
    }
}
