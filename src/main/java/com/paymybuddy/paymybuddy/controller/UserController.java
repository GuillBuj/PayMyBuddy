package com.paymybuddy.paymybuddy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.paymybuddy.paymybuddy.dto.UserDTO;
import com.paymybuddy.paymybuddy.service.UserService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

    @Autowired
    private final UserService userService;

    @GetMapping(produces = "application/json")
    public ResponseEntity<UserDTO> getUserById(@RequestParam int id) {
            UserDTO userDTO = userService.getUserById(id);
            return ResponseEntity.ok(userDTO);
    }
}
