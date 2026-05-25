package com.example.bankcards.controller;

import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.service.UserManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    UserManager userManager;

    @InjectMocks
    UserController userController;

    @Test
    void findUserById_delegates() {
        UserDTO dto = UserDTO.builder().id(1L).build();
        when(userManager.findById(1L)).thenReturn(dto);

        assertThat(userController.findUserById(1L)).isSameAs(dto);
    }

    @Test
    void findAllUsers_delegates() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserDTO> page = new PageImpl<>(List.of());
        when(userManager.findAll(pageable)).thenReturn(page);

        assertThat(userController.findAllUsers(pageable)).isSameAs(page);
    }

    @Test
    void searchUsers_delegates() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserDTO> page = new PageImpl<>(List.of());
        when(userManager.findByKeyFieldsContaining("jane", pageable)).thenReturn(page);

        assertThat(userController.searchUsers("jane", pageable)).isSameAs(page);
    }

    @Test
    void findAllUsersByRole_delegates() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserDTO> page = new PageImpl<>(List.of());
        when(userManager.findAllByRole("ADMIN", pageable)).thenReturn(page);

        assertThat(userController.findAllUsersByRole("ADMIN", pageable)).isSameAs(page);
    }

    @Test
    void activateUser_delegates() {
        userController.activateUser(1L);

        verify(userManager).activateUser(1L);
    }

    @Test
    void deactivateUser_delegates() {
        userController.deactivateUser(1L);

        verify(userManager).deactivateUser(1L);
    }

    @Test
    void createUser_delegates() {
        UserDTO dto = UserDTO.builder().alias("jane").build();
        UserDTO returned = UserDTO.builder().id(1L).alias("jane").build();
        when(userManager.create(dto)).thenReturn(returned);

        assertThat(userController.createUser(dto)).isSameAs(returned);
    }

    @Test
    void update_delegates() {
        UserDTO dto = UserDTO.builder().id(1L).build();
        UserDTO returned = UserDTO.builder().id(1L).build();
        when(userManager.update(1L, dto)).thenReturn(returned);

        assertThat(userController.update(1L, dto)).isSameAs(returned);
    }

    @Test
    void remove_delegates() {
        userController.remove(1L);

        verify(userManager).remove(1L);
    }
}
