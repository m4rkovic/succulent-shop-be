package com.m4rkovic.succulent_shop.service;

import com.m4rkovic.succulent_shop.dto.UserDTO;
import com.m4rkovic.succulent_shop.entity.User;
import com.m4rkovic.succulent_shop.enumerator.Role;
import com.m4rkovic.succulent_shop.exceptions.*;
import com.m4rkovic.succulent_shop.mapper.UserMapper;
import com.m4rkovic.succulent_shop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserValidationService validationService;
    private final UserMapper userMapper;

    // FIND ALL
    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        log.debug("Retrieving all users!");
        return userRepository.findAll();
    }
    @Override
    @Transactional(readOnly = true)
    public Page<User> findAllPaginated(Pageable pageable) {
        log.debug("Retrieving all users with pagination! Page: {}, Size: {}",
                pageable.getPageNumber(), pageable.getPageSize());
        return userRepository.findAll(pageable);
    }



    // FIND BY ID
    @Override
    @Transactional(readOnly = true)
    public User findById(Long id) {
        log.debug("Retrieving user with id: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User not found with id: %d", id)));
    }

    // SAVE
    @Override
    @Transactional
    public User save(String email, String firstname, String lastname,
                     String address, Role role, String password) {
        UserDTO userDTO = createUserDTO(email, firstname, lastname, address, role, password);

        validationService.validateUserDTO(userDTO);

        try {
            User user = userMapper.toEntity(userDTO);
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new CreationException("Failed to create user due to data integrity violation", e);
        }
    }

    // UPDATE
    @Override
    @Transactional
    public User update(Long id, UserDTO userDTO) {
        log.debug("Updating user with id: {}", id);
        User existingUser = findById(id);
        validationService.validateUserDTO(userDTO, true); // Pass true for update

        try {
            userMapper.updateEntityFromDTO(existingUser, userDTO);
            return userRepository.save(existingUser);
        } catch (DataIntegrityViolationException e) {
            throw new UpdateException(String.format("Failed to update user with id: %d", id), e);
        }
    }

    // DELETE BY ID
    @Override
    @Transactional
    public void deleteById(Long userId) {
        log.debug("Deleting user with id: {}", userId);
        findById(userId); // Check if user exists

        try {
            userRepository.deleteById(userId);
        } catch (DataIntegrityViolationException e) {
            throw new DeleteException(
                    String.format("Cannot delete user with id: %d due to existing references", userId), e);
        }
    }

    // FIND BY EMAIL
    @Override
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        log.debug("Retrieving user with email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User not found with email: %s", email)));
    }

    // SEARCH
//    @Override
//    @Transactional(readOnly = true)
//    public Page<User> searchUsers(UserSearchCriteria criteria, int page, int size) {
//        return searchService.search(criteria, page, size);
//    }
//


    // CURRENT USER
    @Override
    @Transactional(readOnly = true)
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {
            throw new UnauthorizedException("No authenticated user found");
        }

        String email = authentication.getName();
        return findByEmail(email);
    }
    private UserDTO createUserDTO(String email, String firstname, String lastname,
                                  String address, Role role, String password) {
        return UserDTO.builder()
                .email(email)
                .firstname(firstname)
                .lastname(lastname)
                .address(address)
                .role(role != null ? role.name() : null)
                .password(password)
                .build();
    }
}