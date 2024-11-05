package com.m4rkovic.succulent_shop.service;

import com.m4rkovic.succulent_shop.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {

    public List<User> findAll();

    public User findById(Long id);

    public User save(User user);

    public void deleteById(Long userId);

}
