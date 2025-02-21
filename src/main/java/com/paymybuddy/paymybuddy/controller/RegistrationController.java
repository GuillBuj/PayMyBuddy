package com.paymybuddy.paymybuddy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.paymybuddy.paymybuddy.dto.RegisterUserDTO;
import com.paymybuddy.paymybuddy.exception.AlreadyExistsException;
import com.paymybuddy.paymybuddy.service.UserService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/register")
@Slf4j
public class RegistrationController {
    
    @Autowired
    private UserService userService;

    @GetMapping
    public String showRegistrationForm(Model model) {
        model.addAttribute("userDTO", new RegisterUserDTO());
        return "register";
    }
    
    @PostMapping
    public String registerUser(@Valid @ModelAttribute RegisterUserDTO userDTO, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            log.error("Binding has error:" + bindingResult.getAllErrors());
            return "register";
        }
        
        if(userService.existsByUsername(userDTO.getUsername())){
            model.addAttribute("errorMessage", "Username already taken");
            return "register";
        }
        
        if(userService.existsByEmail(userDTO.getEmail())){
            model.addAttribute("errorMessage", "Email already used");
            return "register";
        }

        
            userService.createUser(userDTO);
            log.debug("*** User created ***");
            return "redirect:/login";
        }
}
