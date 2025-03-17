package com.paymybuddy.paymybuddy.service;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.paymybuddy.paymybuddy.model.User;
import com.paymybuddy.paymybuddy.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

/**
     * Loads the user details by email for authentication.
     * 
     * This method is called by Spring Security during the authentication process. It retrieves
     * the user from the repository using the provided email. If the user is found, a `UserDetails`
     * object is returned. If the user is not found, a `UsernameNotFoundException` is thrown.
     *
     * @param email The email of the user trying to log in.
     * @return A `UserDetails` object containing the user's email, password, and authorities.
     * @throws UsernameNotFoundException If the user is not found in the repository.
     */
@Service
@Primary
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("* Login attempt: {}" + email);
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("* User not found: " + email);
                    return new UsernameNotFoundException("User not found: " + email);
                });

        log.info("* User found: {}" + user.getEmail());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.emptyList() // Pas de rôles
        );
    }
}


