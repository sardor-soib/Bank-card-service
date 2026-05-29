package com.example.bankcards.service.impl;

import com.example.bankcards.dto.CreateUserDTO;
import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.UserService;
import com.example.bankcards.util.Role;
import com.example.bankcards.util.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.sql.SQLException;

@Service
@Validated
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isExists(Long id) {
        logger.info("Checking if user exists with {}", id);
        return userRepository.existsById(id);
    }

    @Override
    public UserDTO create(CreateUserDTO dto) {
        logger.info("Creating user {}", dto.email());
        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.password()));
        return userMapper.toDTO(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        logger.info("Finding user by id {}", id);
        return userMapper.toDTO(getUserEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> findAll(Pageable pageable) {
        logger.info("Fetching all users from repository");
        return userMapper.toDTOPage(userRepository.findAll(pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> findAllByRole(String role, Pageable pageable) {
        logger.info("Fetching all users from repository for role {}", role);
        return userMapper.toDTOPage(userRepository.findAllByRole(Role.valueOf(role), pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> findByKeyFieldsContaining(String query, Pageable pageable) throws ResourceNotFoundException {
        try {
            logger.info("Finding users by key fields containing: {}", query);
            return userMapper.toDTOPage(userRepository.findByKeyFieldsContainingIgnoreCase(query, pageable));
        } catch (SQLException e) {
            throw new ResourceNotFoundException("Error finding users by key fields containing: " + query, e);
        }
    }

    @Override
    public UserDTO update(Long id, UserDTO dto) {
        logger.info("Updating user with id {}", id);
        User user = getUserEntityById(id);
        if (dto.fullName() != null) user.setFullName(dto.fullName());
        if (dto.role() != null) user.setRole(Role.valueOf(dto.role()));
        return userMapper.toDTO(userRepository.save(user));
    }

    @Override
    public void remove(Long id) {
        logger.info("Removing user with id {}", id);
        requireUserExists(id);
        userRepository.deleteById(id);
    }

    @Override
    public void activateUser(Long id) {
        logger.info("Activating user with id {}", id);
        User user = getUserEntityById(id);
        user.activateUser();
        userRepository.save(user);
    }

    @Override
    public void deactivateUser(Long id) {
        logger.info("Deactivating user with id {}", id);
        User user = getUserEntityById(id);
        user.deactivateUser();
        userRepository.save(user);
    }

    private void requireUserExists(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
    }

    private User getUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }
}