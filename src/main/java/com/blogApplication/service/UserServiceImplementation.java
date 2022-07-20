package com.blogApplication.service;

import com.blogApplication.model.User;
import com.blogApplication.repository.CommentsRepositry;
import com.blogApplication.repository.UserRepositry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImplementation implements UserService{
    @Autowired
    UserRepositry userRepositry;
    @Override
    public void saveUser(User user) {
        userRepositry.save(user);
    }
    @Override
    public List<User> findAllAuthors() {
        return userRepositry.findAll();
    }
}
