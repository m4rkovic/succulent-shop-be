package com.m4rkovic.succulent_shop.mapper;

import com.m4rkovic.succulent_shop.dto.UserDTO;
import com.m4rkovic.succulent_shop.entity.User;
import com.m4rkovic.succulent_shop.enumerator.Role;
import com.m4rkovic.succulent_shop.enumerator.ToolType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class UserMapper {

    public User toEntity(UserDTO dto) {
        if (dto == null) {
            return null;
        }

        return User.builder()
                .id(dto.getId())
                .firstname(dto.getFirstname())
                .lastname(dto.getLastname())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .role(StringUtils.isNotBlank(dto.getRole()) ?
                        Role.valueOf(dto.getRole().toUpperCase()) : null)
                .address(dto.getAddress())
                .build();
    }

    public UserDTO toDTO(User entity) {
        if (entity == null) {
            return null;
        }

        return UserDTO.builder()
                .id(entity.getId())
                .firstname(entity.getFirstname())
                .lastname(entity.getLastname())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .role(entity.getRole() != null ?
                        entity.getRole().name() : null)
                .address(entity.getAddress())
                .build();
    }

    public void updateEntityFromDTO(User entity, UserDTO dto) {
        if (entity == null || dto == null) {
            return;
        }

        if (StringUtils.isNotBlank(dto.getFirstname())) {
            entity.setFirstname(dto.getFirstname());
        }
        if (StringUtils.isNotBlank(dto.getLastname())) {
            entity.setLastname(dto.getLastname());
        }
        if (StringUtils.isNotBlank(dto.getEmail())) {
            entity.setEmail(dto.getEmail());
        }
        if (StringUtils.isNotBlank(dto.getPassword())) {
            entity.setPassword(dto.getPassword());
        }
        if (StringUtils.isNotBlank(dto.getRole())) {
            entity.setRole(Role.valueOf(dto.getRole().toUpperCase()));
        }
        if (StringUtils.isNotBlank(dto.getAddress())) {
            entity.setAddress(dto.getAddress());
        }
    }

    public List<UserDTO> toDTOList(List<User> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
