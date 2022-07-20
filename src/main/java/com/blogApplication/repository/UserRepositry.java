package com.blogApplication.repository;

import com.blogApplication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepositry extends JpaRepository<User,Integer> {
    User findUserByName(String name);
    @Query(value = "select name from users",nativeQuery = true)
    List<String> getAllUsersNames();
}
