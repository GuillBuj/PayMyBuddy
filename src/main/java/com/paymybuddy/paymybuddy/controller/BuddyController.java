package com.paymybuddy.paymybuddy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paymybuddy.paymybuddy.dto.BuddyConnectionDTO;
import com.paymybuddy.paymybuddy.service.UserService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/buddies")
@AllArgsConstructor
public class BuddyController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<String> addBuddyConnection(@RequestBody BuddyConnectionDTO buddyConnectionDTO){
        userService.addBuddy(buddyConnectionDTO);
        return ResponseEntity.ok("Buddy connection successfully added");
    }

    @DeleteMapping
    public ResponseEntity<String> removeBuddyConnection(@RequestBody BuddyConnectionDTO buddyConnectionDTO){
        userService.removeBuddy(buddyConnectionDTO);
        return ResponseEntity.ok("Buddy connection successfully removed");
    }
}
