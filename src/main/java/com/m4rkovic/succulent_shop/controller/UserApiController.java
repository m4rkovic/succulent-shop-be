package com.m4rkovic.succulent_shop.controller;

import com.m4rkovic.succulent_shop.dto.UserDTO;
import com.m4rkovic.succulent_shop.entity.User;
import com.m4rkovic.succulent_shop.enumerator.Role;
import com.m4rkovic.succulent_shop.enumerator.ToolType;
import com.m4rkovic.succulent_shop.exceptions.InvalidDataException;
import com.m4rkovic.succulent_shop.repository.UserRepository;
import com.m4rkovic.succulent_shop.response.UserResponse;
import com.m4rkovic.succulent_shop.service.UserService;
import com.m4rkovic.succulent_shop.service.UserValidationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "User Controller", description = "User management APIs")
@CrossOrigin
@Slf4j
public class UserApiController {

    private final UserService userService;
    private final UserValidationService validationService;

    // FIND BY ID
    @Operation(summary = "Get a user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(UserResponse.fromEntity(user));
    }

    // FIND ALL
    @Operation(summary = "Get all users")
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.findAll()
                .stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    // ADD USER
    @Operation(summary = "Create a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserDTO userDto) {
        log.debug("Creating new user with data: {}", userDto);

        try {
            // Use the simpler validation method for creation
            validationService.validateUserDTO(userDto);

            // Convert Role enum
            Role role = userDto.getRole() != null ?
                    Role.valueOf(userDto.getRole().toUpperCase()) : null;

            User savedUser = userService.save(
                    userDto.getEmail(),
                    userDto.getFirstname(),
                    userDto.getLastname(),
                    userDto.getAddress(),
                    role,
                    userDto.getPassword()
            );

            UserResponse response = UserResponse.fromEntity(savedUser);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(savedUser.getId())
                    .toUri();

            return ResponseEntity.created(location).body(response);

        } catch (IllegalArgumentException e) {
            log.error("Invalid enum value in user creation request", e);
            throw new InvalidDataException("Invalid user data: " + e.getMessage());
        }
    }
    
//    @PostMapping
//    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserDTO userDto) {
//        log.debug("Creating new user with data: {}", userDto);
//
//        try {
//            // Validate using validation service
//            validationService.validateUserDTO(userDto);
//
//            // Convert Role enum
//            Role role = userDto.getRole() != null ?
//                    Role.valueOf(userDto.getRole().toUpperCase()) : null;
//
//            User savedUser = userService.save(
//                    userDto.getEmail(),
//                    userDto.getFirstname(),
//                    userDto.getLastname(),
//                    userDto.getAddress(),
//                    role,
//                    userDto.getPassword()
//            );
//
//            UserResponse response = UserResponse.fromEntity(savedUser);
//            URI location = ServletUriComponentsBuilder
//                    .fromCurrentRequest()
//                    .path("/{id}")
//                    .buildAndExpand(savedUser.getId())
//                    .toUri();
//
//            return ResponseEntity.created(location).body(response);
//
//        } catch (IllegalArgumentException e) {
//            log.error("Invalid enum value in user creation request", e);
//            throw new InvalidDataException("Invalid user data: " + e.getMessage());
//        }
//    }

    // UPDATE
    @Operation(summary = "Update a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody UserDTO userDto) {
        log.debug("Updating user {} with data: {}", id, userDto);

        // The service now handles the validation with isUpdate flag
        User updatedUser = userService.update(id, userDto);
        return ResponseEntity.ok(UserResponse.fromEntity(updatedUser));
    }

    // DELETE
    @Operation(summary = "Delete a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long id) {
        log.debug("Deleting user: {}", id);
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // FIND BY EMAIL
    @Operation(summary = "Get a user by email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(
            @Parameter(description = "User email", required = true)
            @PathVariable String email) {
        User user = userService.findByEmail(email);
        return ResponseEntity.ok(UserResponse.fromEntity(user));
    }


//    @Operation(summary = "Search and filter users")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Successfully retrieved users"),
//            @ApiResponse(responseCode = "400", description = "Invalid input parameters")
//    })
//    @GetMapping("/search")
//    public ResponseEntity<Page<UserResponse>> searchUsers(
//            @RequestParam(required = false) String searchTerm,
//            @RequestParam(required = false) String role,
//            @RequestParam(required = false, defaultValue = "id") String sortBy,
//            @RequestParam(required = false, defaultValue = "asc") String sortDirection,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size) {
//
//        UserSearchCriteria criteria = UserSearchCriteria.builder()
//                .searchTerm(searchTerm)
//                .role(role != null ? Role.valueOf(role.toUpperCase()) : null)
//                .sortBy(sortBy)
//                .sortDirection(sortDirection)
//                .build();
//
//        Page<User> userPage = userService.searchUsers(criteria, page, size);
//        Page<UserResponse> responsePage = userPage.map(UserResponse::fromEntity);
//
//        return ResponseEntity.ok(responsePage);
//    }

}