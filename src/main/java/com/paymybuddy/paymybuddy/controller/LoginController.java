package com.paymybuddy.paymybuddy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller for handling user login processes, including displaying the login form 
 * and processing the login authentication.
 */
@Controller
@RequestMapping("/login")
@Slf4j
public class LoginController {
    
    /**
     * Displays the login form. If an error message is present in the session, 
     * it is added to the model and displayed to the user.
     * 
     * @param model The Spring MVC model used to pass attributes to the view.
     * @param request The `HttpServletRequest` object that provides access to the user session 
     *                and other HTTP request information.
     * @return The name of the view to be displayed. In this case, it returns "login", 
     *         which refers to a `login.html` or other view template for displaying the login form.
     */
    @GetMapping
    public String showLoginForm(Model model, HttpServletRequest request) {
        
        String errorMessage = (String) request.getSession().getAttribute("errorMessage");

        if (errorMessage != null) {
            model.addAttribute("errorMessage", "Erreur. Vérifiez mot de passe et adresse mail");
            request.getSession().removeAttribute("errorMessage");
        }
        
        return "login";
    }
}
