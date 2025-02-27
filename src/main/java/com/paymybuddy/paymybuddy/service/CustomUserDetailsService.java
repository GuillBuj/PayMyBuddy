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


