package com.blogApplication.service;

import com.blogApplication.model.User;

import java.util.List;

public interface UserService {
    void saveUser(User user);
    List<User> findAllAuthors();
}
