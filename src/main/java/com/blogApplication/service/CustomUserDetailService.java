package com.blogApplication.service;

import com.blogApplication.model.User;
import com.blogApplication.repository.UserRepositry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {
    @Autowired
    private UserRepositry userRepositry;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user=userRepositry.findUserByName(username);
        if(user==null){
            throw new UsernameNotFoundException("No User");
        }
        return new CustomUserDetail(user);
    }
}
