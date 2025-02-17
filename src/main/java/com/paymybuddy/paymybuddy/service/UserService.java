package com.paymybuddy.paymybuddy.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.paymybuddy.paymybuddy.dto.BuddyConnectionDTO;
import com.paymybuddy.paymybuddy.dto.RegisterUserDTO;
import com.paymybuddy.paymybuddy.dto.UserDTO;
import com.paymybuddy.paymybuddy.exception.AlreadyExistsException;
import com.paymybuddy.paymybuddy.exception.NotFoundException;
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

    @Transactional
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
        user.setBalance(BigDecimal.ZERO);
        user.setBuddies(new HashSet<>());
        user.setDateCreated(LocalDateTime.now());
        
        return userRepository.save(user);
    }

    public UserDTO getUserById(int id){
        User user = userRepository.findById(id).orElse(null);
        if (user == null){
            throw new NotFoundException("User not found with id " + id);
        }
        return new UserDTO(user.getUserName(), user.getEmail());
    }

    //on considère qu'il n'y a pas de réciprocité ni d'acceptation d'ajout
    @Transactional
    public void addBuddy(BuddyConnectionDTO buddyConnection){
        User user = userRepository.findByEmail(buddyConnection.userEmail())
                .orElseThrow(()-> new NotFoundException("User not found with email " + buddyConnection.userEmail()));
                
        User buddy = userRepository.findByEmail(buddyConnection.buddyEmail())
                .orElseThrow(()-> new NotFoundException("Buddy not found with email " + buddyConnection.buddyEmail()));

        if (user.getBuddies().contains(buddy)){
            throw new AlreadyExistsException("Buddy connection already exists");
        };
        
        user.getBuddies().add(buddy);

        userRepository.save(user);
    }

    @Transactional
    public void removeBuddy(BuddyConnectionDTO buddyConnection){
        User user = userRepository.findByEmail(buddyConnection.userEmail())
                .orElseThrow(()-> new NotFoundException("User not found with email " + buddyConnection.userEmail()));
                
        User buddy = userRepository.findByEmail(buddyConnection.buddyEmail())
                .orElseThrow(()-> new NotFoundException("Buddy not found with email " + buddyConnection.buddyEmail()));
        
        if (!user.getBuddies().contains(buddy)){
            throw new NotFoundException("Buddy connection does not exist");
        }
        
        user.getBuddies().remove(buddy);

        userRepository.save(user);
    }
}
