package com.paymybuddy.paymybuddy.dto;

import java.util.Set;

import com.paymybuddy.paymybuddy.model.User;

public record UserDTO(
    String userName,
    String email,
    Set<User> buddies
) {

}
