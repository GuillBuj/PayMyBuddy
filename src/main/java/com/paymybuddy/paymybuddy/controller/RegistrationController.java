package com.paymybuddy.paymybuddy.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.paymybuddy.paymybuddy.dto.RegisterUserDTO;
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
        log.debug("- GET /register");

        model.addAttribute("userDTO", new RegisterUserDTO());
        return "register";
    }
    
    @PostMapping
    public String registerUser(@Valid @ModelAttribute("userDTO") RegisterUserDTO userDTO,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        log.debug("- POST /register : {}, {}", userDTO.getEmail(), userDTO.getUsername());

        if (bindingResult.hasErrors()) {
            log.error("Validation errors: {}", bindingResult.getAllErrors());
            return "register";
        }
        
        if(userService.existsByUsername(userDTO.getUsername())){
            log.error("Username already taken: {}", userDTO.getUsername());
            model.addAttribute("errorUsername", "Username already taken");
            return "register";
        }
        
        if(userService.existsByEmail(userDTO.getEmail())){
            log.error("Email already used: {}", userDTO.getPassword());
            model.addAttribute("errorEmail", "Email already used");
            return "register";
        }

        userService.createUser(userDTO);
        log.debug("*** User created ***");
        redirectAttributes.addFlashAttribute("successMessage", "Account successfully created!");
        
        return "redirect:/login";
    }
}
