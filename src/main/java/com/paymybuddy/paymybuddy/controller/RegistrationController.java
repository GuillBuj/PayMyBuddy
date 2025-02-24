package com.paymybuddy.paymybuddy.controller;

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
        model.addAttribute("userDTO", new RegisterUserDTO());
        return "register";
    }
    
    @PostMapping
    public String registerUser(@Valid @ModelAttribute RegisterUserDTO userDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            log.error("Binding has error:" + bindingResult.getAllErrors());
            return "register";
        }
        
        if(userService.existsByUsername(userDTO.getUsername())){
            redirectAttributes.addFlashAttribute("errorMessage", "Username already taken");
            return "redirect:/register";
        }
        
        if(userService.existsByEmail(userDTO.getEmail())){
            redirectAttributes.addFlashAttribute("errorMessage", "Email already taken");
            return "redirect:/register";
        }

        userService.createUser(userDTO);
        log.debug("*** User created ***");
        redirectAttributes.addFlashAttribute("successMessage", "Account successfully created!");
        return "redirect:/login";
    }
}
