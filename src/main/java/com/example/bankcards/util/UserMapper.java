package com.example.bankcards.util;

import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.entity.User;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDTO(@NotNull User user);

    User toEntity(@NotNull UserDTO userDTO);

    List<UserDTO> toDTOList(@NotNull List<User> users);

    List<User> toEntityList(@NotNull List<UserDTO> userDTOs);

    default Page<UserDTO> toDTOPage(@NotNull Page<User> userPage) {
        return userPage.map(this::toDTO);
    }
}

