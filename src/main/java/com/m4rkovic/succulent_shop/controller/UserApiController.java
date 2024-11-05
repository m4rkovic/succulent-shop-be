package com.m4rkovic.succulent_shop.controller;

import com.m4rkovic.succulent_shop.entity.User;
import com.m4rkovic.succulent_shop.repository.UserRepository;
import com.m4rkovic.succulent_shop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@CrossOrigin
public class UserApiController {

    private final UserRepository userRepository;
    private final UserService userService;

    @GetMapping("/{id}")
    public User getUsers(@PathVariable Long id) {
        return userService.findById(id);
    }

    @GetMapping
    public List<User> getUser() {
        return userService.findAll();
    }

    @PutMapping("/{id}")
    public ResponseEntity updateProduct(@PathVariable Long id, @RequestBody User user) {
        User currentUser = userService.findById(id);
        currentUser.setFirstname(currentUser.getFirstname());
        currentUser.setLastname(currentUser.getLastname());
        currentUser.setEmail(currentUser.getEmail());
        currentUser.setPassword(currentUser.getPassword());
        currentUser.setRole(currentUser.getRole());
//        currentUser.setAddress(currentUser.getAddress());
        currentUser = userRepository.save(user);

        return ResponseEntity.ok(currentUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.ok().build();
    }

}
