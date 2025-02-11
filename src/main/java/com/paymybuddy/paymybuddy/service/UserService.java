package com.paymybuddy.paymybuddy.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.paymybuddy.paymybuddy.dto.RegisterUserDTO;
import com.paymybuddy.paymybuddy.dto.UserDTO;
import com.paymybuddy.paymybuddy.exception.AlreadyExistsException;
import com.paymybuddy.paymybuddy.model.User;
import com.paymybuddy.paymybuddy.repository.UserRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public User createUser(RegisterUserDTO userDTO){
        
        String lowerCaseUserName = userDTO.userName().toLowerCase();
        if(userRepository.existsByUserName(lowerCaseUserName)){
            log.error("Username already taken: {}", lowerCaseUserName);
            throw new AlreadyExistsException("Username already taken: " + lowerCaseUserName);
        }
        
        String normalizedEmail = userDTO.email().trim().toLowerCase();
        if(userRepository.existsByEmail(normalizedEmail)){
            log.error("Email already used: {}", normalizedEmail);
            throw new AlreadyExistsException("Email already used:" + normalizedEmail);
        }
        
        User user = new User();
        user.setUserName(lowerCaseUserName);
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(userDTO.password()));
        user.setBalance(0);
        user.setDateCreated(LocalDateTime.now());

        return userRepository.save(user);
    }

    public UserDTO getUserById(int id){
        User user = userRepository.getById(id);
        return new UserDTO(user.getUserName(), user.getEmail());
    }
}
