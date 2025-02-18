package com.paymybuddy.paymybuddy.service;

import org.springframework.stereotype.Service;

import com.paymybuddy.paymybuddy.dto.BalanceOperationDTO;
import com.paymybuddy.paymybuddy.exception.NotFoundException;
import com.paymybuddy.paymybuddy.model.User;
import com.paymybuddy.paymybuddy.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class BalanceService {

    UserRepository userRepository;

    public void updateBalance(BalanceOperationDTO operation, boolean plus){
        User user = userRepository.findByEmail(operation.userEmail())
            .orElseThrow(()-> new NotFoundException("User not found with email " + operation.userEmail()));

        if(plus){
            user.setBalance(user.getBalance().add(operation.amount()));
        } else {
            user.setBalance(user.getBalance().subtract(operation.amount()));
        }
        
        userRepository.save(user);
    }
}
