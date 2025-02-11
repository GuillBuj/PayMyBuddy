package com.paymybuddy.paymybuddy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.paymybuddy.paymybuddy.dto.RegisterUserDTO;
import com.paymybuddy.paymybuddy.dto.UserDTO;
import com.paymybuddy.paymybuddy.model.User;
import com.paymybuddy.paymybuddy.service.UserService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    // @PostMapping
    // public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterUserDTO userDTO){
    //     try {
    //         userService.createUser(userDTO);
    //         return ResponseEntity.ok("User created successfully");
    //     } catch (Exception e) {
    //         return ResponseEntity.status(400).body("Error creating user" + e.getMessage());
    //     }
    // }

    @GetMapping(produces = "application/json")
    public UserDTO getUserById(@RequestParam int id) {
        return userService.getUserById(id);
    }


}
