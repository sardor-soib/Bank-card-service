package com.example.bankcards.service.impl;

import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.Role;
import com.example.bankcards.util.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    UserMapper userMapper;

    @InjectMocks
    UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = org.mockito.Mockito.mock(User.class);
        when(user.getId()).thenReturn(1L);
    }

    @Test
    void isExists_delegatesToRepository() {
        when(userRepository.existsById(1L)).thenReturn(true);

        assertThat(userService.isExists(1L)).isTrue();
    }

    @Test
    void create_savesEntityAndReturnsDto() {
        UserDTO inputDto = UserDTO.builder().alias("jane").build();
        UserDTO returnedDto = UserDTO.builder().id(1L).alias("jane").build();
        User mapped = org.mockito.Mockito.mock(User.class);
        User saved = org.mockito.Mockito.mock(User.class);

        when(userMapper.toEntity(inputDto)).thenReturn(mapped);
        when(userRepository.save(mapped)).thenReturn(saved);
        when(userMapper.toDTO(saved)).thenReturn(returnedDto);

        assertThat(userService.create(inputDto)).isSameAs(returnedDto);
    }

    @Test
    void findById_existing_returnsDto() {
        UserDTO dto = UserDTO.builder().id(1L).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(dto);

        assertThat(userService.findById(1L)).isSameAs(dto);
    }

    @Test
    void findById_missing_throws() {
        when(userRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(404L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findAll_delegatesToRepositoryAndMaps() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> page = new PageImpl<>(List.of(user));
        Page<UserDTO> dtoPage = new PageImpl<>(List.of(UserDTO.builder().id(1L).build()));

        when(userRepository.findAll(pageable)).thenReturn(page);
        when(userMapper.toDTOPage(page)).thenReturn(dtoPage);

        assertThat(userService.findAll(pageable)).isSameAs(dtoPage);
    }

    @Test
    void findAllByRole_parsesRoleAndDelegates() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> page = new PageImpl<>(List.of(user));
        Page<UserDTO> dtoPage = new PageImpl<>(List.of());

        when(userRepository.findAllByRole(Role.ADMIN, pageable)).thenReturn(page);
        when(userMapper.toDTOPage(page)).thenReturn(dtoPage);

        assertThat(userService.findAllByRole("ADMIN", pageable)).isSameAs(dtoPage);
    }

    @Test
    void findAllByRole_invalidRole_throws() {

        Throwable thrown = catchThrowable(() -> userService.findAllByRole("BAD_ROLE", PageRequest.of(0, 10)));

        assertThat(thrown).as("Expected IllegalArgumentException for invalid role").isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    void findByKeyFieldsContaining_delegatesToRepository() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> page = new PageImpl<>(List.of(user));
        Page<UserDTO> dtoPage = new PageImpl<>(List.of());

        when(userRepository.findByKeyFieldsContainingIgnoreCase("jane", pageable)).thenReturn(page);
        when(userMapper.toDTOPage(page)).thenReturn(dtoPage);

        assertThat(userService.findByKeyFieldsContaining("jane", pageable)).isSameAs(dtoPage);
    }

    @Test
    void update_existing_saves() {
        UserDTO dto = UserDTO.builder().id(1L).alias("jane").build();
        User mapped = org.mockito.Mockito.mock(User.class);
        User saved = org.mockito.Mockito.mock(User.class);
        UserDTO returned = UserDTO.builder().id(1L).build();

        when(userRepository.existsById(1L)).thenReturn(true);
        when(userMapper.toEntity(dto)).thenReturn(mapped);
        when(userRepository.save(mapped)).thenReturn(saved);
        when(userMapper.toDTO(saved)).thenReturn(returned);

        assertThat(userService.update(1L, dto)).isSameAs(returned);
    }

    @Test
    void update_missing_throws() {
        when(userRepository.existsById(404L)).thenReturn(false);

        Throwable thrown = catchThrowable(() -> userService.update(404L, UserDTO.builder().id(404L).build()));

        assertThat(thrown).as("Expected ResourceNotFoundException for missing user").isInstanceOf(ResourceNotFoundException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void remove_existing_deletes() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.remove(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void remove_missing_throws() {
        when(userRepository.existsById(404L)).thenReturn(false);

        assertThatThrownBy(() -> userService.remove(404L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void activateUser_existingUser_activatesAndSaves() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.activateUser(1L);

        verify(user).activateUser();
        verify(userRepository).save(user);
    }

    @Test
    void deactivateUser_existingUser_deactivatesAndSaves() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deactivateUser(1L);

        verify(user).deactivateUser();
        verify(userRepository).save(user);
    }

    @Test
    void activateUser_missing_throws() {
        when(userRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.activateUser(404L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
