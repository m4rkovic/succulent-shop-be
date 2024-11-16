package com.m4rkovic.succulent_shop.service;

import com.m4rkovic.succulent_shop.dto.UserDTO;
import com.m4rkovic.succulent_shop.entity.User;
import com.m4rkovic.succulent_shop.enumerator.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public interface UserService {
    List<User> findAll();

    User findById(Long id);
    public Page<User> findAllPaginated(Pageable pageable);
    User save(String email, String firstname, String lastname,
                     String address, Role role, String password);

    User update(Long id, UserDTO userDTO);

    void deleteById(Long userId);

    User findByEmail(String email);
}