package com.blogApplication.controllers;

import com.blogApplication.model.PhoneNumber;
import com.blogApplication.model.Posts;
import com.blogApplication.model.User;
import com.blogApplication.repository.UserRepositry;
import com.blogApplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Controller
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepositry userRepositry;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @GetMapping("/loginPage")
    public String viewLoginPage(){
        return "loginPage";
    }
    @GetMapping("/registerUser")
    public String registerUser(){
        return "registerUserPage";
    }
    @PostMapping("/saveAndRegisterUser")
    public String saveAndRegisterUser(User user, Model model, @RequestParam("reEnterPassword") String reEnteredPassword){
        User oldUser=userRepositry.findUserByName(user.getName());
        if(Objects.equals(user.getName(), "") || Objects.equals(user.getEmail(), "") || Objects.equals(user.getPassword(), "")){
            model.addAttribute("errorMessage","Invalid details please fill all the details");
            return "registerUserPage";
        }
        if(!Objects.equals(user.getPassword(), reEnteredPassword)){
            model.addAttribute("errorMessage","Passwords are not matched");
            return "registerUserPage";
        }
        if(oldUser!=null){
            model.addAttribute("errorMessage","username already exists");
            return "registerUserPage";
        }
        String password=user.getPassword();
        user.setPassword(passwordEncoder. encode(password));
        userService.saveUser(user);
        return "redirect:/loginPage";
    }
    @GetMapping("/accessDenied")
    public String error(){
        return "access-denied";
    }

    @RequestMapping("/api/v1/checkPhoneNumber/{mobilePhoneNumber}")
    public String data(@PathVariable("mobilePhoneNumber") String phoneNumber){
        System.out.println(phoneNumber);
        return  null;
    }
    @GetMapping("/test")
    public String get(Model model){
        return "phone";
    }
}
