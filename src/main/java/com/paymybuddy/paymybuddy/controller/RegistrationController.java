package com.paymybuddy.paymybuddy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.paymybuddy.paymybuddy.dto.RegisterUserDTO;
import com.paymybuddy.paymybuddy.dto.UserResponseDTO;
import com.paymybuddy.paymybuddy.model.User;
import com.paymybuddy.paymybuddy.service.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/register")
public class RegistrationController {
    
    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody RegisterUserDTO userDTO){
        User createdUser = userService.createUser(userDTO);
    
        UserResponseDTO response = new UserResponseDTO(
            createdUser.getId(),
            createdUser.getUserName(),
            createdUser.getEmail()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
