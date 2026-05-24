package com.example.bankcards.service;

import com.example.bankcards.dto.UserDTO;;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

public interface UserManager {

    boolean isExists(@NotNull Long id);

    void activateUser(@NotNull Long userId);

    void deactivateUser(@NotNull Long userId);

    UserDTO create(@NotNull UserDTO userDTO);

    UserDTO findById(@NotNull Long id);

    Page<UserDTO> findByKeyFieldsContaining(@NotNull String query, Pageable pageable) throws ResourceNotFoundException;

    Page<UserDTO> findAllByRole(@NotNull String role, Pageable pageable);

    Page<UserDTO> findAll(Pageable pageable);

    UserDTO update(@NotNull Long id, @NotNull UserDTO userDTO);

    void remove(@NotNull Long id);
}
