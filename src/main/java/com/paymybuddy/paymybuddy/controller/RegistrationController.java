package com.paymybuddy.paymybuddy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.paymybuddy.paymybuddy.dto.RegisterUserDTO;
import com.paymybuddy.paymybuddy.dto.UserResponseDTO;
import com.paymybuddy.paymybuddy.exception.AlreadyExistsException;
import com.paymybuddy.paymybuddy.model.User;
import com.paymybuddy.paymybuddy.service.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/register")
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
            return "register";
        }

        try {
            userService.createUser(userDTO);
            return "redirect:/login";
        } catch (AlreadyExistsException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }
}
