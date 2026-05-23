package com.example.bankcards.service;

import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.exception.ServiceException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.Role;
import com.example.bankcards.util.UserMapper;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.sql.SQLException;

@Service
@Validated
@Transactional
public class UserService implements UserManager {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public boolean isExists(@NotNull Long id) {
        logger.info("Checking if user exists with {}", id);
        return userRepository.existsById(id);
    }

    @Override
    public UserDTO create(@NotNull UserDTO dto) {
        logger.info("Creating user {}", dto);
        User user = userMapper.toEntity(dto);
        return userMapper.toDTO(userRepository.save(user));
    }

    @Override
    public UserDTO findById(@NotNull Long id) {
        logger.info("Finding user by id {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID %d not found".formatted(id)));
        return userMapper.toDTO(user);
    }

    @Override
    public Page<UserDTO> findAll(Pageable pageable) {
        logger.info("Fetching all users from repository");
        Page<User> users = userRepository.findAll(pageable);
        return userMapper.toDTOPage(users);
    }

    @Override
    public Page<UserDTO> findAllByRole(@NotNull String role, Pageable pageable) {
        logger.info("Fetching all users from repository for role {}", role);
        Page<User> users = userRepository.findAllByRole(Role.valueOf(role), pageable);
        return userMapper.toDTOPage(users);
    }

    @Override
    public UserDTO update(@NotNull Long id, @NotNull UserDTO dto) {
        logger.info("Updating user with id {}", id);
        if (!isExists(id)) {
            logger.info("User with id {} is not exists", id);
            throw new ResourceNotFoundException(String.format("User not found with id: %d", id));
        }
        User user = userMapper.toEntity(dto);
        return userMapper.toDTO(userRepository.save(user));
    }

    @Override
    public void remove(@NotNull Long id) {
        logger.info("Removing user with id {}", id);
        if (!isExists(id)) {
            throw new ResourceNotFoundException(String.format("User not found with id: %d", id));
        }
        userRepository.deleteById((id));
    }

    @Override
    public Page<UserDTO> findByKeyFieldsContaining(@NotNull String query, Pageable pageable) throws ServiceException {
        try {
            logger.info("Finding users by key fields containing: {}", query);
            Page<User> users = userRepository.findByKeyFieldsContainingIgnoreCase(query, pageable);
            return userMapper.toDTOPage(users);
        } catch (SQLException e) {
            throw new ServiceException("Error finding users by key fields containing: " + query, e);
        }

    }
}

