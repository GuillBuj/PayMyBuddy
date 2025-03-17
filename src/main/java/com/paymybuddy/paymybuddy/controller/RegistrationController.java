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

/**
 * Controller for handling user registration processes, including displaying the registration form 
 * and processing the registration request.
 */
@Controller
@RequestMapping("/register")
@Slf4j
public class RegistrationController {
    
    @Autowired
    private UserService userService;
    
    /**
     * Displays the registration form for a new user.
     * 
     * @param model The Spring MVC model used to pass attributes to the view.
     * @return The name of the view to be displayed. In this case, it returns "register", 
     *         which corresponds to the registration form view.
     */
    @GetMapping
    public String showRegistrationForm(Model model) {
        log.debug("- GET /register");

        model.addAttribute("userDTO", new RegisterUserDTO());
        return "register";
    }
    
    /**
     * Processes the user registration form submission, validates the data, and creates a new user account.
     * 
     * @param userDTO The `RegisterUserDTO` object containing the data submitted by the user via the registration form.
     * @param bindingResult The `BindingResult` object used to check for validation errors.
     * @param model The Spring MVC model used to pass attributes to the view.
     * @param redirectAttributes The `RedirectAttributes` object used to pass temporary attributes during redirects.
     * @return A redirect to the login page if the registration is successful. If validation errors occur, 
     *         it returns the registration form view with error messages.
     */
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
        
        userService.createUser(userDTO);
        log.debug("*** User created ***");
        redirectAttributes.addFlashAttribute("successMessage", "Compte créé avec succès");
        
        return "redirect:/login";
    }
}
