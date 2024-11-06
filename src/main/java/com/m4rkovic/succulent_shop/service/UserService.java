package com.m4rkovic.succulent_shop.service;

import com.m4rkovic.succulent_shop.dto.UserDTO;
import com.m4rkovic.succulent_shop.entity.User;
import com.m4rkovic.succulent_shop.enumerator.Role;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public interface UserService {
    List<User> findAll();

    User findById(Long id);

    User save(String email, String firstname, String lastname,
                     String address, Role role, String password);

    User update(Long id, UserDTO userDTO);

    void deleteById(Long userId);

    User findByEmail(String email);
}