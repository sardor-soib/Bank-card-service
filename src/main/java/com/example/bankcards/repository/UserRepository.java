package com.example.bankcards.repository;

import com.example.bankcards.entity.User;
import com.example.bankcards.util.Role;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findBySub(String sub);

    Page<User> findAllByRole(Role role, Pageable pageable);

    // Only searches from the beginning of the field
    @Query("""
            SELECT u FROM User u
            WHERE LOWER(u.fullName) LIKE LOWER(CONCAT(:query, '%'))
               OR LOWER(u.email)    LIKE LOWER(CONCAT(:query, '%'))
            """)
    Page<User> findByKeyFieldsContainingIgnoreCase(@NotNull String query, Pageable pageable) throws java.sql.SQLException;

}