package com.example.bankcards.controller;

import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.service.UserManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@Tag(name = "User Controller", description = "User Management Controller")
@RequestMapping("/api/v1/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserManager userManager;

    @Autowired
    public UserController(UserManager userManager) {
        this.userManager = userManager;
    }

    @Operation(summary = "Find a user by ID", description = "Retrieves a user by their unique identifier")
    @GetMapping("/{id}")
    public UserDTO findUserById(@NotNull @PathVariable Long id) {
        return userManager.findById(id);
    }

    @Operation(summary = "Find all users", description = "Retrieves a list of all users")
    @GetMapping
    public Page<UserDTO> findAllUsers(Pageable pageable) {
        return userManager.findAll(pageable);
    }

    @Operation(summary = "Search users", description = "Searches for users based on key fields and role")
    @GetMapping("/search")
    public Page<UserDTO> searchUsers(@RequestParam(name = "query") String query, Pageable pageable) {
        return userManager.findByKeyFieldsContaining(query, pageable);
    }

    @Operation(summary = "Find all users by role", description = "Retrieves a list of users by their role")
    @GetMapping("/role/{role}")
    public Page<UserDTO> findAllUsersByRole(@PathVariable String role, Pageable pageable) {
        return userManager.findAllByRole(role, pageable);
    }

    @Operation(summary = "Activate user", description = "Activates a user by their unique identifier")
    @PatchMapping("/{id}/activate")
    public void activateUser(@PathVariable Long id) {
        userManager.activateUser(id);
    }

    @Operation(summary = "Deactivate user", description = "Deactivates a user by their unique identifier")
    @PatchMapping("/{id}/deactivate")
    public void deactivateUser(@PathVariable Long id) {
        userManager.deactivateUser(id);
    }

    @Operation(summary = "Create a new user", description = "Creates a new user with the provided details")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO createUser(@NotNull @Valid @RequestBody UserDTO userDTO) {
        return userManager.create(userDTO);
    }

    @Operation(summary = "Update a user", description = "Updates an existing user with the provided details")
    @PutMapping("/{id}")
    public UserDTO update(@NotNull @PathVariable Long id, @NotNull @Valid @RequestBody UserDTO userDTO) {
        return userManager.update(id, userDTO);
    }

    @Operation(summary = "Delete a user", description = "Deletes a user by their unique identifier")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@NotNull @PathVariable Long id) {
        userManager.remove(id);
    }
}
